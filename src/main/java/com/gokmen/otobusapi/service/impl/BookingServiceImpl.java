package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.*;
import com.gokmen.otobusapi.repository.entities.*;
import com.gokmen.otobusapi.repository.entities.Ticket.TicketStatus;
import com.gokmen.otobusapi.repository.record.Booking.CreateBooking;
import com.gokmen.otobusapi.repository.record.Booking.ResponseBooking;
import com.gokmen.otobusapi.repository.record.Ticket.CreateTicket;
import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;
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
    private final TravellerRepository travellerRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, VoyagesRepository voyagesRepository, TravellerRepository travellerRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.voyagesRepository = voyagesRepository;
        this.travellerRepository = travellerRepository;
    }

    @Override
    public List<Booking> getBookings() {
        return bookingRepository.findAll();
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
        request.travellers().forEach(traveller -> {
            if (!seatExists(bus, traveller.seat()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat does not exists");
            if (isSeatOccupied(voyage, traveller.seat()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The selected seat already occupied.");
        });
        request.travellers().forEach(traveller -> {
            if (request.travellers().stream().anyMatch(traveller1 -> traveller.seat() == traveller1.seat() && !traveller.equals(traveller1)))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't select the same seats.");
        });

        ArrayList<Travellers> travellers = new ArrayList<>();

        request.travellers().forEach(traveller1 -> {
            if (!isGenderCompatible(traveller1, voyage))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat is adjacent to different gender");
            if (genderLock(traveller1, voyage))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The gender lock happened.");
            Travellers traveller = new Travellers();

            String travellerName = requiredText(traveller1.travellerName(), "Traveller name is required");
            String travellerSurname = requiredText(traveller1.travellerSurname(), "Traveller surname is required");
            String gender = requiredText(traveller1.gender(), "Gender is required");
            String storedIdentification;

            if (traveller1.isForeign()) {
                storedIdentification = "Foreigner";
            } else {
                String identificationNumber = traveller1.identificationNumber();
                if (identificationNumber == null || !identificationNumber.matches("\\d{11}"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identification number must contain 11 digits");
                if (!validId(identificationNumber))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid identification number");

                String first;
                String last;

                first = traveller1.identificationNumber().substring(0, 2);
                last = traveller1.identificationNumber().substring(9);

                storedIdentification = first + "*******" + last;
            }

            traveller.setTravellerName(travellerName);
            traveller.setTravellerSurname(travellerSurname);
            traveller.setGender(gender);
            traveller.setForeign(traveller1.isForeign());
            traveller.setIndentityNumber(storedIdentification);

            traveller.setBusId(bus);
            traveller.setVoyageId(voyage);
            traveller.setSeat(traveller1.seat());
            traveller.setFirstStation(voyage.getFirstStation());
            traveller.setLastStation(voyage.getLastStation());
            traveller.setTravelStart(voyage.getStartDate());
            traveller.setTravelEnd(voyage.getEndDate());
            traveller.setActive(true);

            travellers.add(traveller);
        });

        if (travellers.isEmpty())
            throw new  ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one traveller is required");

        ArrayList<Ticket> tickets = new ArrayList<>();

        booking.setUser(user);
        booking.setVoyages(voyage);
        booking.setBookingReference(generateBookingReference());
        booking.setCreatedAt(Instant.now());

        travellers.forEach(travellers1 -> {
            CreateTicket createTicket = new CreateTicket(booking, travellers1, voyage.getVoyagePrice(), TicketStatus.CONFIRMED);
            Ticket ticket = Ticket.fromCreate(createTicket);
            tickets.add(ticket);
        });

        booking.setTickets(tickets);
        Booking savedBooking = bookingRepository.save(booking);
        return Booking.toResponse(savedBooking);
    }

    @Override
    @Transactional
    public ResponseBooking deactivateBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setActive(false);
        Booking savedBooking = bookingRepository.save(booking);
        return Booking.toResponse(savedBooking);
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
        return travellerRepository.findActiveTravellersForJourney(selectedVoyage.getBus().getBus_id(), selectedVoyage.getJourneyNo()).stream().filter(traveller -> traveller.getSeat() == requestedSeat).anyMatch(traveller ->
                segmentsOverlap(selectedVoyage.getFirstStation(), selectedVoyage.getLastStation(), traveller.getFirstStation(), traveller.getLastStation()));
    }

    private boolean segmentsOverlap(int firstStart, int firstEnd, int secondStart, int secondEnd) {
        int normalizedFirstStart = Math.min(firstStart, firstEnd);
        int normalizedFirstEnd = Math.max(firstStart, firstEnd);
        int normalizedSecondStart = Math.min(secondStart, secondEnd);
        int normalizedSecondEnd = Math.max(secondStart, secondEnd);

        return normalizedFirstStart < normalizedSecondEnd && normalizedSecondStart < normalizedFirstEnd;
    }

    private boolean isGenderCompatible(CreateTraveller request, Voyages voyages) {
        String requestedGender = request.gender().trim().toLowerCase(Locale.ROOT);

        if (!requestedGender.equals("erkek") && !requestedGender.equals("kadın"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender");

        List<SchemaDetail> schemaDetails = voyages.getBus().getSchemaHeader().getSchemaDetails();

        List<Travellers> relevantTravellers = travellerRepository.findActiveTravellersForJourney(voyages.getBus().getBus_id(), voyages.getJourneyNo()).stream().filter(traveller -> segmentsOverlap(voyages.getFirstStation(), voyages.getLastStation(), traveller.getFirstStation(), traveller.getLastStation())).toList(); // Sondaki filtre gerekiyor mu yoksa gerekmiyor mu kontrolet.

        for (Travellers travellers : relevantTravellers) {
            for (SchemaDetail row : schemaDetails) {
                boolean sitNextToTraveller = (request.seat() == row.getColumn1() && travellers.getSeat() == row.getColumn2()) || (request.seat() == row.getColumn2() && travellers.getSeat() == row.getColumn1()) || (request.seat() == row.getColumn4() && travellers.getSeat() == row.getColumn5()) || (request.seat() == row.getColumn5() && travellers.getSeat() == row.getColumn4());
                if (sitNextToTraveller) {
                    String existingGender = travellers.getGender().trim().toLowerCase(Locale.ROOT);
                    if (!requestedGender.equals(existingGender))
                        return false;
                }
            }
        }
        return true;
    }

    private boolean genderLock(CreateTraveller request, Voyages voyages) {
        List<SchemaDetail> schemaDetails = voyages.getBus().getSchemaHeader().getSchemaDetails();
        List<Travellers> relevantTravellers = travellerRepository.findActiveTravellersForJourney(voyages.getBus().getBus_id(), voyages.getJourneyNo()).stream().filter(traveller -> traveller.getVoyageId().getVoyageId() == voyages.getVoyageId()).toList();

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
        List<Travellers> upperTravellers = relevantTravellers.stream().filter(traveller -> traveller.getSeat() < request.seat()).sorted(Comparator.comparingInt(Travellers::getSeat).reversed()).toList();
        List<Travellers> lowerTravellers = relevantTravellers.stream().filter(traveller -> traveller.getSeat() > request.seat()).sorted(Comparator.comparingInt(Travellers::getSeat)).toList();

        // Eğer kullanılmayan bir koltuk varsa sayaç durmuyor.

        for (Travellers upperTraveller : upperTravellers) {
            String genderU = upperTraveller.getGender().trim().toLowerCase(Locale.ROOT);
            for (int l = 0; l < 3; l++) {
                int upperPos = position[0] - (l + 1);
                if (position[1] == 0) {
                    if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn1()) {
                        if (schemaDetails.get(upperPos).getColumn1() != 0 && isSeatOccupied(voyages, schemaDetails.get(upperPos).getColumn1())) {
                            if (genderU.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                up++;
                            else upBreakPoint = true;
                        } else upBreakPoint = true;
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
                }
                if (position[1] == 3) {
                    if (upperPos >= 0 && upperTraveller.getSeat() == schemaDetails.get(upperPos).getColumn4()) {
                        if (schemaDetails.get(upperPos).getColumn4() != 0) {
                            if (genderU.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                up++;
                            else upBreakPoint = true;
                        } else upBreakPoint = true;
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
                }
            }
        }

        for (Travellers lowerTraveller : lowerTravellers) {
            String genderD = lowerTraveller.getGender().trim().toLowerCase(Locale.ROOT);
            for (int l = 0; l < 3; l++) {
                int lowerPos = position[0] + (l + 1);
                if (position[1] == 0) {
                    if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn1()) {
                        if (schemaDetails.get(lowerPos).getColumn1() != 0) {
                            if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                down++;
                            else downBreakPoint = true;
                        } else downBreakPoint = true;
                    }
                }
                if (position[1] == 1) {
                    if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn2()) {
                        if (schemaDetails.get(lowerPos).getColumn2() != 0) {
                            if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                down++;
                            else downBreakPoint = true;
                        } else downBreakPoint = true;
                    }
                }
                if (position[1] == 3) {
                    if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn4()) {
                        if (schemaDetails.get(lowerPos).getColumn4() != 0) {
                            if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                down++;
                            else downBreakPoint = true;
                        } else downBreakPoint = true;
                    }
                }
                if (position[1] == 4) {
                    if (lowerPos < schemaDetails.size() && lowerTraveller.getSeat() == schemaDetails.get(lowerPos).getColumn5()) {
                        if (schemaDetails.get(lowerPos).getColumn5() != 0) {
                            if (genderD.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
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

        return up >= 3 || down >= 3 || (up + down) >= 3;
    }

    private String requiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return value.trim();
    }
}
