package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.*;
import com.gokmen.otobusapi.repository.entities.*;
import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.ResponseTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.UpdateTraveller;
import com.gokmen.otobusapi.service.TravellersService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TravellersServiceImpl implements TravellersService {

    private final TravellerRepository travellerRepository;


    public TravellersServiceImpl(TravellerRepository travellerRepository) {
        this.travellerRepository = travellerRepository;
    }


    @Override
    @Transactional
    public void setTraveller(CreateTraveller request) {
        Travellers traveller = Travellers.fromCreate(request);

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
        return condition1 && condition2 && condition3;
    }

    @Override
    @Transactional
    public void updateTraveller(int travelerId, UpdateTraveller request) {
        Travellers traveller = travellerRepository.findById(travelerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Traveller not found"));
        //There is no update condition for now.
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
        return travellerRepository.findAll().stream().map(Travellers::toResponse).toList();
    }

    @Override
    public Optional<Travellers> findById(int travellerId) {
        if (travellerRepository.findById(travellerId).isPresent())
            return travellerRepository.findById(travellerId);
        else
            return Optional.empty();
    }
}

