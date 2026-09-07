package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.*;
import com.gokmen.otobusapi.repository.entities.*;
import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.ResponseTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.UpdateTraveller;
import com.gokmen.otobusapi.service.TravellersService;
import com.gokmen.otobusapi.service.VoyagesService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class TravellersServiceImpl implements TravellersService {

    private final TravellerRepository travellerRepository;
    private final BussRepository bussRepository;
    private final VoyagesRepository voyagesRepository;
    private final VoyagesService voyagesService; //Başka bir service e service çağırmak doğru mu bak.


    public TravellersServiceImpl(TravellerRepository travellerRepository, BussRepository bussRepository, VoyagesRepository voyagesRepository, VoyagesService voyagesService) {
        this.travellerRepository = travellerRepository;
        this.bussRepository = bussRepository;
        this.voyagesRepository = voyagesRepository;
        this.voyagesService = voyagesService;
    }


    @Override
    @Transactional
    public void setTraveller(CreateTraveller request) {
        Voyages voyage = voyagesRepository.findById(request.voyageId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voyage not found"));
        if (!voyage.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voyage is not active");
        Buss bus = voyage.getBus();
        if (bus == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voyage does not have a bus");
        if (!bus.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bus is not active");
        if (!seatExists(bus, request.seat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat does not exists");
        if (isSeatOccupied(voyage, request.seat()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The selected seat already occupied.");
        if (!sameGender(request, voyage))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat is adjacent to different gender");
        if (genderLock(request, voyage))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The gender lock happened.");

        String travellerName = requiredText(request.travellerName(), "Traveller name is required");
        String travellerSurname = requiredText(request.travellerSurname(), "Traveller surname is required");
        String gender = requiredText(request.gender(), "Gender is required");
        String storedIdentification;

        if (request.isForeign()) {
            storedIdentification = "Foreigner";
        } else {
            String identificationNumber = request.identificationNumber();
            if (identificationNumber == null || !identificationNumber.matches("\\d{11}"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identification number must contain 11 digits");
            if (!validId(identificationNumber))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid identification number");

            String first;
            String last;

            first = request.identificationNumber().substring(0, 2);
            last = request.identificationNumber().substring(9);

            storedIdentification = first + "*******" + last;
        }

        Travellers traveller = new Travellers();

        traveller.setTravellerName(travellerName);
        traveller.setTravellerSurname(travellerSurname);
        traveller.setGender(gender);
        traveller.setForeign(request.isForeign());
        traveller.setIndentityNumber(storedIdentification);

        traveller.setBusId(bus);
        traveller.setVoyageId(voyage);
        traveller.setSeat(request.seat());
        traveller.setFirstStation(voyage.getFirstStation());
        traveller.setLastStation(voyage.getLastStation());
        traveller.setTravelStart(voyage.getStartDate());
        traveller.setTravelEnd(voyage.getEndDate());
        traveller.setActive(true);

        travellerRepository.save(traveller);
    }

    private boolean validId(String identificationNumber) {
        int[] numbers = new int[11];

        for (int i = 0; i < 11; i++) {
            numbers[i] = Integer.parseInt(identificationNumber.substring(i, (i + 1)));
        }
        boolean condition1 = (numbers[0] + numbers[1] + numbers[2] + numbers[3] + numbers[4] + numbers[5] + numbers[6] + numbers[7] + numbers[8] + numbers[9]) % 10 == numbers[10];
        boolean condition2 = (((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 7) + ((numbers[1] + numbers[3] + numbers[5] + numbers[7]) * 9)) % 10 == numbers[9];
        boolean condition3 = ((numbers[0] + numbers[2] + numbers[4] + numbers[6] + numbers[8]) * 8) % 10 == numbers[10];
        if (condition1 && condition2 && condition3) return true;
        else return false;
    }

    @Override
    @Transactional
    public void updateTraveller(int travelerId, UpdateTraveller request) {
        Travellers traveller = travellerRepository.findById(travelerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Traveller not found"));

        if (!traveller.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Traveller is not Active");

        if (!seatExists(traveller.getBusId(), request.seat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The selected seat does not exists");
        if (isSeatOccupied(traveller.getVoyageId(), request.seat()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The selected seat already occupied.");

        traveller.setSeat(request.seat());
        travellerRepository.save(traveller);
    }

    @Override
    @Transactional
    public void deactivateTraveller(int travellerId) {
        Travellers traveller = travellerRepository.findById(travellerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Traveller not found"));
        traveller.setActive(false);
        travellerRepository.save(traveller);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseTraveller> findAllTravellers() {
        return travellerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getOccupiedSeats(int voyageId) {
        Voyages selectedVoyage = voyagesRepository.findById(voyageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voyage not found"));
        return travellerRepository.findActiveTravellersForJourney(selectedVoyage.getBus().getBus_id(), selectedVoyage.getJourneyNo()).stream().filter(existingTraveller ->
                segmentsOverlap(selectedVoyage.getFirstStation(), selectedVoyage.getLastStation(), existingTraveller.getFirstStation(), existingTraveller.getLastStation())
        ).map(Travellers::getSeat).distinct().sorted().toList();
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

    private boolean sameGender(CreateTraveller request, Voyages voyages) {
        String requestedGender = request.gender().trim().toLowerCase(Locale.ROOT);

        if (!requestedGender.equals("erkek") && !requestedGender.equals("kadın"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender");

        List<SchemaDetail> schemaDetails = voyages.getBus().getSchemaHeader().getSchemaDetails();

        List<Travellers> relevantTravellers = travellerRepository.findActiveTravellersForJourney(voyages.getBus().getBus_id(), voyages.getJourneyNo()).stream().filter(traveller -> traveller.getVoyageId() == voyages).toList();

        for (Travellers travellers : relevantTravellers) {
            for (SchemaDetail row : schemaDetails) {
                boolean sitNextToTraveller = (request.seat() == row.getColumn1() && travellers.getSeat() == row.getColumn2()) || (request.seat() == row.getColumn2() && travellers.getSeat() == row.getColumn1()) || (request.seat() == row.getColumn4() && travellers.getSeat() == row.getColumn5()) || (request.seat() == row.getColumn5() && travellers.getSeat() == row.getColumn4());
                if (sitNextToTraveller) {
                    String existingGender = travellers.getGender().trim().toLowerCase(Locale.ROOT);
                    return requestedGender.equals(existingGender);
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

        for (Travellers traveller : relevantTravellers) {
            String gender = traveller.getGender().trim().toLowerCase(Locale.ROOT);
            for (int l = 0; l < 3; l++) {
                int upperPos = position[0] - (l + 1);
                int lowerPos = position[0] + (l + 1);
                if (position[1] == 0) {
                    if (position[0] - (l + 1) < (position[0] - l) && upperPos >= 0) {
                        if (schemaDetails.get(upperPos).getColumn1() != 0) {
                            if (traveller.getSeat() == schemaDetails.get(upperPos).getColumn1()) {
                                if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                                    up++;
                                else upBreakPoint = true;
                            }
                        }

                    }
                    if ((position[0] + l) < position[0] + (l + 1) && lowerPos < schemaDetails.size()) {
                        if (schemaDetails.get(lowerPos).getColumn1() != 0) {
                            if (traveller.getSeat() == schemaDetails.get(lowerPos).getColumn1()) {
                                if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                                    down++;
                                else downBreakPoint = true;
                            }
                        }
                    }
                }
                if (position[1] == 1) {
                    if (upperPos >= 0 && schemaDetails.get(upperPos).getColumn2() != 0 && traveller.getSeat() == schemaDetails.get(upperPos).getColumn2()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                            up++;
                        else upBreakPoint = true;
                    } // schemaDetails.get(upperPos).getColumn2() != 0 kondisyonunda flaglenmesi gerekyiyor.
                    if (lowerPos < schemaDetails.size() && schemaDetails.get(lowerPos).getColumn2() != 0 && traveller.getSeat() == schemaDetails.get(lowerPos).getColumn2()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                            down++;
                        else downBreakPoint = true;
                    }
                }
                if (position[1] == 3) {
                    if (upperPos >= 0 && schemaDetails.get(upperPos).getColumn4() != 0 && traveller.getSeat() == schemaDetails.get(upperPos).getColumn4()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                            up++;
                        else upBreakPoint = true;
                    }
                    if (lowerPos < schemaDetails.size() && schemaDetails.get(lowerPos).getColumn4() != 0 && traveller.getSeat() == schemaDetails.get(lowerPos).getColumn4()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                            down++;
                        else downBreakPoint = true;
                    }
                }
                if (position[1] == 4) {
                    if (upperPos >= 0 && schemaDetails.get(upperPos).getColumn5() != 0 && traveller.getSeat() == schemaDetails.get(upperPos).getColumn5()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !upBreakPoint)
                            up++;
                        else upBreakPoint = true;
                    }
                    if (lowerPos < schemaDetails.size() && schemaDetails.get(lowerPos).getColumn5() != 0 && traveller.getSeat() == schemaDetails.get(lowerPos).getColumn5()) {
                        if (gender.equals(request.gender().trim().toLowerCase(Locale.ROOT)) && !downBreakPoint)
                            down++;
                        else downBreakPoint = true;
                    }
                }
            }
        }
        return up >= 3 || down >= 3 || (up + down) >= 3;
    }



    private String requiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return value.trim();
    }

    @Override
    public Optional<Travellers> findById(int travellerId) {
        if (travellerRepository.findById(travellerId).isPresent())
            return travellerRepository.findById(travellerId);
        else
            return Optional.empty();
    }

    @Override
    public Optional<Travellers> findByBus(String plateNumber) {
        if (bussRepository.findAllTravellersByPlateNumber(plateNumber).isPresent())
            return bussRepository.findAllTravellersByPlateNumber(plateNumber);
        else
            return Optional.empty();
    }

    private ResponseTraveller toResponse(Travellers traveller) {
        return new ResponseTraveller(
                traveller.getTraveller_id(),
                traveller.getTravellerName(),
                traveller.getTravellerSurname(),
                traveller.getGender(),
                traveller.isForeign(),
                traveller.getIndentityNumber(),
                Buss.toResponse(traveller.getBusId()),
                traveller.getSeat(),
                voyagesService.toResponse(traveller.getVoyageId()),
                traveller.getFirstStation(),
                traveller.getLastStation(),
                traveller.getTravelStart(),
                traveller.getTravelEnd(),
                traveller.isActive()
        );
    }
}

