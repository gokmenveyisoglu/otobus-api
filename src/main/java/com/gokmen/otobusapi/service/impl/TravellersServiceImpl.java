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
import java.util.Optional;

@Service
public class TravellersServiceImpl implements TravellersService {

    private final TravellerRepository travellerRepository;
    private final BussRepository bussRepository;
    private final VoyagesRepository voyagesRepository;
    private final VoyagesService voyagesService; //Başka türlü yapmayı bak


    public TravellersServiceImpl(TravellerRepository travellerRepository, BussRepository bussRepository, VoyagesRepository voyagesRepository,VoyagesService voyagesService) {
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
                segmentsOverlap(selectedVoyage.getFirstStation(), selectedVoyage.getLastStation(),traveller.getFirstStation(), traveller.getLastStation()));
    }

    private boolean segmentsOverlap(int firstStart, int firstEnd, int secondStart, int secondEnd) {
        int normalizedFirstStart = Math.min(firstStart, firstEnd);
        int normalizedFirstEnd = Math.max(firstStart, firstEnd);
        int normalizedSecondStart = Math.min(secondStart, secondEnd);
        int normalizedSecondEnd = Math.max(secondStart, secondEnd);

        return normalizedFirstStart < normalizedSecondEnd && normalizedSecondStart < normalizedFirstEnd;
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

