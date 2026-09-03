package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Travellers;
import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.ResponseTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.UpdateTraveller;

import java.util.List;
import java.util.Optional;

public interface TravellersService {


    void setTraveller(CreateTraveller request);
    void updateTraveller(int travelerId, UpdateTraveller request);
    void deactivateTraveller(int travellerId);
    List<ResponseTraveller> findAllTravellers();
    List<Integer> getOccupiedSeats(int voyageId);
    Optional<Travellers> findById(int travellerId);
    Optional<Travellers> findByBus(String plateNumber);
}
