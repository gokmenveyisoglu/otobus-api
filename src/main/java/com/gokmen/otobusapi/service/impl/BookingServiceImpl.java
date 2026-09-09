package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.*;
import com.gokmen.otobusapi.repository.entities.*;
import com.gokmen.otobusapi.repository.entities.Ticket.TicketStatus;
import com.gokmen.otobusapi.repository.record.Booking.CreateBooking;
import com.gokmen.otobusapi.repository.record.Booking.ResponseBooking;
import com.gokmen.otobusapi.repository.record.Ticket.CreateTicket;
import com.gokmen.otobusapi.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class BookingServiceImpl implements BookingService {

    private static final String REFERENCE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final VoyagesRepository voyagesRepository;
    private final TicketRepository ticketRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, VoyagesRepository voyagesRepository, TicketRepository ticketRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.voyagesRepository = voyagesRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<ResponseBooking> getBookings() {
        return bookingRepository.findAll().stream().map(Booking::toResponse).toList();
    }

    @Override
    @Transactional
    public ResponseBooking setBooking(CreateBooking request) {
        Booking booking = new Booking();
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!user.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not active");
        Voyages voyage = voyagesRepository.findById(request.voyageId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voyage not found"));
        if (!voyage.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voyage not active");
        Buss bus = voyage.getBus();
        if (bus == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voyage does not have a bus");
        if (!bus.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bus is not active");
        request.tickets().forEach(ticket -> {
            if (!seatExists(bus, ticket.seat()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat does not exists");
            if (isSeatOccupied(voyage, ticket.seat()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The selected seat already occupied.");
        });
        request.tickets().forEach(ticket -> {
            if (request.tickets().stream().anyMatch(ticket1 -> ticket.seat() == ticket1.seat() && ticket.travellers() != ticket1.travellers()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't select the same seats.");
        });

        ArrayList<Ticket> tickets = new ArrayList<>();

        if (genderLock(request.tickets(), voyage))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The gender lock happened.");

        request.tickets().forEach(ticket -> {
            if (!isGenderCompatible(ticket, voyage))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat is adjacent to different gender");
            Ticket newTicket = Ticket.fromCreate(ticket, booking, voyage);

            tickets.add(newTicket);
        });

        if (tickets.isEmpty())
            throw new  ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one ticket is required");

        booking.setUser(user);
        booking.setVoyages(voyage);
        booking.setBookingReference(generateBookingReference());
        booking.setCreatedAt(Instant.now());

        booking.setTickets(tickets);
        Booking savedBooking = bookingRepository.save(booking);
        return Booking.toResponse(savedBooking);
    }

    @Override
    @Transactional
    public ResponseBooking deactivateBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setActive(false);
        booking.getTickets().forEach(ticket -> ticket.setStatus(TicketStatus.CANCELLED));
        Booking savedBooking = bookingRepository.save(booking);
        return Booking.toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getOccupiedSeats(int voyageId) {
        Voyages selectedVoyage = voyagesRepository.findById(voyageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voyage not found"));
        return ticketRepository.findActiveTicketsForJourney(selectedVoyage.getBus().getBus_id(),selectedVoyage.getJourneyNo(), TicketStatus.CONFIRMED).stream().filter(existingTraveller ->
                segmentsOverlap(selectedVoyage.getFirstStation(), selectedVoyage.getLastStation(), existingTraveller.getFirstStation(), existingTraveller.getLastStation())
        ).map(Ticket::getSeat).distinct().sorted().toList();
    }

    private String generateBookingReference() {
        for (int attempt = 0; attempt <10; attempt++) {
            StringBuilder reference = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                int index = random.nextInt(REFERENCE_CHARACTERS.length());
                reference.append(REFERENCE_CHARACTERS.charAt(index));
            }

            String candidate = reference.toString();

            if (!bookingRepository.existsByBookingReference(candidate)) {
                return candidate;
            }
        }

        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Could not generate a booking reference. Please try again.");
    }

    private boolean validId(String identificationNumber) {
        int[] numbers = new int[11];

        for (int i = 0; i < 11; i++) {
            numbers[i] = Integer.parseInt(identificationNumber.substring(i, (i + 1)));
        }
        boolean condition1 = (numbers[0] + numbers[1] + numbers[2] + numbers[3] + numbers[4] + numbers[5] + numbers[6] + numbers[7] + numbers[8] + numbers[9]) % 10 == numbers[10];
        boolean condition2 = (((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 7) + ((numbers[1] + numbers[3] + numbers[5] + numbers[7]) * 9)) % 10 == numbers[9];
        boolean condition3 = ((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 8) % 10 == numbers[10];
        return condition1 && condition2 && condition3;
    }

    private boolean seatExists(Buss buss, int requestedSeat) {
        if (requestedSeat <= 0 || buss.getSchemaHeader() == null) {
            return false;
        }
        return buss.getSchemaHeader().getSchemaDetails().stream().anyMatch(row ->
                row.getColumn1() == requestedSeat || row.getColumn2() == requestedSeat || row.getColumn4() == requestedSeat || row.getColumn5() == requestedSeat
        );
    }

    private boolean isSeatOccupied(Voyages selectedVoyage, int requestedSeat) {
        return ticketRepository.findActiveTicketsForJourney(selectedVoyage.getBus().getBus_id(), selectedVoyage.getJourneyNo(), TicketStatus.CONFIRMED).stream().filter(ticket -> ticket.getSeat() == requestedSeat).anyMatch(ticket ->
                segmentsOverlap(selectedVoyage.getFirstStation(), selectedVoyage.getLastStation(), ticket.getFirstStation(), ticket.getLastStation()));
    }

    private boolean segmentsOverlap(int firstStart, int firstEnd, int secondStart, int secondEnd) {
        int normalizedFirstStart = Math.min(firstStart, firstEnd);
        int normalizedFirstEnd = Math.max(firstStart, firstEnd);
        int normalizedSecondStart = Math.min(secondStart, secondEnd);
        int normalizedSecondEnd = Math.max(secondStart, secondEnd);

        return normalizedFirstStart < normalizedSecondEnd && normalizedSecondStart < normalizedFirstEnd;
    }

    private boolean isGenderCompatible(CreateTicket request, Voyages voyages) {
        String requestedGender = request.travellers().gender().trim().toLowerCase(Locale.ROOT);

        if (!requestedGender.equals("erkek") && !requestedGender.equals("kadın"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender");

        List<SchemaDetail> schemaDetails = voyages.getBus().getSchemaHeader().getSchemaDetails();

        List<Ticket> relevantTickets = ticketRepository.findActiveTicketsForJourney(voyages.getBus().getBus_id(), voyages.getJourneyNo(), TicketStatus.CONFIRMED).stream().filter(ticket -> segmentsOverlap(voyages.getFirstStation(), voyages.getLastStation(), ticket.getFirstStation(), ticket.getLastStation())).toList();

        for (Ticket tickets : relevantTickets) {
            for (SchemaDetail row : schemaDetails) {
                boolean sitNextToTraveller = (request.seat() == row.getColumn1() && tickets.getSeat() == row.getColumn2()) || (request.seat() == row.getColumn2() && tickets.getSeat() == row.getColumn1()) || (request.seat() == row.getColumn4() && tickets.getSeat() == row.getColumn5()) || (request.seat() == row.getColumn5() && tickets.getSeat() == row.getColumn4());
                if (sitNextToTraveller) {
                    String existingGender = tickets.getTraveller().getGender().trim().toLowerCase(Locale.ROOT);
                    if (!requestedGender.equals(existingGender))
                        return false;
                }
            }
        }
        return true;
    }

    private boolean genderLock(List<CreateTicket> requests, Voyages voyages) {
        List<SchemaDetail> schemaDetails = voyages.getBus().getSchemaHeader().getSchemaDetails();
        List<Ticket> relevantTickets = new ArrayList<>(ticketRepository.findActiveTicketsForJourney(voyages.getBus().getBus_id(), voyages.getJourneyNo(), TicketStatus.CONFIRMED).stream().filter(ticket -> ticket.getBooking().getVoyages().getVoyageId() == voyages.getVoyageId()).toList());

        for (CreateTicket request : requests) {
            int up = 0;
            int down = 0;
            boolean upBreakPoint = false;
            boolean downBreakPoint = false;

            int[] position = new int[2];

            for (int j = 0; j < schemaDetails.size(); j++) {
                if (schemaDetails.get(j).getColumn1() == request.seat()) {
                    position[0] = j;
                    position[1] = 0;
                }
                if (schemaDetails.get(j).getColumn2() == request.seat()) {
                    position[0] = j;
                    position[1] = 1;
                }
                if (schemaDetails.get(j).getColumn4() == request.seat()) {
                    position[0] = j;
                    position[1] = 3;
                }
                if (schemaDetails.get(j).getColumn5() == request.seat()) {
                    position[0] = j;
                    position[1] = 4;
                }
            }
            List<Ticket> upperTravellers = relevantTickets.stream().filter(ticket -> ticket.getSeat() < request.seat()).sorted(Comparator.comparingInt(Ticket::getSeat).reversed()).toList();
            List<Ticket> lowerTravellers = relevantTickets.stream().filter(ticket -> ticket.getSeat() > request.seat()).sorted(Comparator.comparingInt(Ticket::getSeat)).toList();

            // Eğer kullanılmayan bir koltuk varsa sayaç durmuyor.

            for (Ticket upperTraveller : upperTravellers) {
                String genderU = upperTraveller.getTraveller().getGender().trim().toLowerCase(Locale.ROOT);
                for (int l = 0; l < 3; l++) {
                    int upperPos = position[0] - (l + 1);
                    if (position[1] == 0) {
                        if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn1()) {
                            if (schemaDetails.get(upperPos).getColumn1() != 0) {
                                if (genderU.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                    up++;
                                else upBreakPoint = true;
                            } else upBreakPoint = true;
                        }
                    }
                    if (position[1] == 1) {
                        if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn2()) {
                            if (schemaDetails.get(upperPos).getColumn2() != 0) {
                                if (genderU.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                    up++;
                                else upBreakPoint = true;
                            } else upBreakPoint = true;
                        } // schemaDetails.get(upperPos).getColumn2() != 0 kondisyonunda flaglenmesi gerekyiyor.
                    }
                    if (position[1] == 3) {
                        if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn4()) {
                            if (schemaDetails.get(upperPos).getColumn4() != 0) {
                                if (genderU.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                    up++;
                                else upBreakPoint = true;
                            } else upBreakPoint = true;
                        }
                    }
                    if (position[1] == 4) {
                        if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn5()) {
                            if (schemaDetails.get(upperPos).getColumn5() != 0) {
                                if (genderU.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                    up++;
                                else upBreakPoint = true;
                            } else upBreakPoint = true;
                        }
                    }
                }
            }

            for (Ticket lowerTraveller : lowerTravellers) {
                String genderD = lowerTraveller.getTraveller().getGender().trim().toLowerCase(Locale.ROOT);
                for (int l = 0; l < 3; l++) {
                    int lowerPos = position[0] + (l + 1);
                    if (position[1] == 0) {
                        if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn1()) {
                            if (schemaDetails.get(lowerPos).getColumn1() != 0) {
                                if (genderD.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                    down++;
                                else downBreakPoint = true;
                            } else downBreakPoint = true;
                        }
                    }
                    if (position[1] == 1) {
                        if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn2()) {
                            if (schemaDetails.get(lowerPos).getColumn2() != 0) {
                                if (genderD.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                    down++;
                                else downBreakPoint = true;
                            } else downBreakPoint = true;
                        }
                    }
                    if (position[1] == 3) {
                        if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn4()) {
                            if (schemaDetails.get(lowerPos).getColumn4() != 0) {
                                if (genderD.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                    down++;
                                else downBreakPoint = true;
                            } else downBreakPoint = true;
                        }
                    }
                    if (position[1] == 4) {
                        if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn5()) {
                            if (schemaDetails.get(lowerPos).getColumn5() != 0) {
                                if (genderD.equals(request.travellers().gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                    down++;
                                else downBreakPoint = true;
                            } else downBreakPoint = true;
                        }
                    }
                }
            }

            /*for (int i = 0; i < relevantTravellers.size(); i++) {
            Travellers upperTraveller = null;
            if (i < upperTravellers.size()) {
                upperTraveller = upperTravellers.get(i);
            }
            Travellers lowerTraveller = null;
            if (i < lowerTravellers.size()) {
                lowerTraveller = lowerTravellers.get(i);
            }

            String genderU = upperTraveller.getGender().trim().toLowerCase(Locale.ROOT);
            String genderD = lowerTraveller.getGender().trim().toLowerCase(Locale.ROOT);
            for (int l = 0; l < 3; l++) {
                int upperPos = position[0] - (l + 1);
                int lowerPos = position[0] + (l + 1);
                if (position[1] == 0) {
                    if (position[0] - (l + 1) < (position[0] - l) && upperPos >= 0) {
                        if (schemaDetails.get(upperPos).getColumn1() != 0) {
                            if (upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn1()) {
                                if (genderU.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                    up++;
                                else upBreakPoint = true;
                            }
                        } else upBreakPoint = true;
                    }
                    if ((position[0] + l) < position[0] + (l + 1) && lowerPos < schemaDetails.size()) {
                        if (schemaDetails.get(lowerPos).getColumn1() != 0) {
                            if (lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn1()) {
                                if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                    down++;
                                else downBreakPoint = true;
                            }
                        } else downBreakPoint = true;
                    }
                }
                if (position[1] == 1) {
                    if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn2()) {
                        if (schemaDetails.get(upperPos).getColumn2() != 0) {
                            if (genderU.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                up++;
                            else upBreakPoint = true;
                        } else upBreakPoint = true;
                    } // schemaDetails.get(upperPos).getColumn2() != 0 kondisyonunda flaglenmesi gerekyiyor.
                    if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn2()) {
                        if (schemaDetails.get(lowerPos).getColumn2() != 0) {
                            if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                down++;
                            else downBreakPoint = true;
                        } else downBreakPoint = true;
                    }
                }
                if (position[1] == 3) {
                    if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn4()) {
                        if (schemaDetails.get(upperPos).getColumn4() != 0) {
                            if (genderU.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                up++;
                            else upBreakPoint = true;
                        } else upBreakPoint = true;
                    }
                    if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn4()) {
                        if (schemaDetails.get(lowerPos).getColumn4() != 0) {
                            if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                down++;
                            else downBreakPoint = true;
                        } else downBreakPoint = true;
                    }
                }
                if (position[1] == 4) {
                    if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn5()) {
                        if (schemaDetails.get(upperPos).getColumn5() != 0) {
                            if (genderU.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                up++;
                            else upBreakPoint = true;
                        } else upBreakPoint = true;
                    }
                    if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn5()) {
                        if (schemaDetails.get(lowerPos).getColumn5() != 0) {
                            if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                down++;
                            else downBreakPoint = true;
                        } else downBreakPoint = true;
                    }
                }
            }
        }*/

            Travellers traveller = Travellers.fromCreate(request.travellers());
            Ticket newTicket = new Ticket();
            newTicket.setTraveller(traveller);
            newTicket.setSeat(request.seat());

            if (up >= 3 || down >= 3 || (up + down) >= 3) {
                return true;
            }
            relevantTickets.add(newTicket);
        }
        return false;
    }

    private String requiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return value.trim();
    }
}
