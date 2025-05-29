package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Travellers;

import java.util.List;
import java.util.Optional;

public interface TravellersService {


    void setTraveller(String voyageId, int busNo, int seatNo, boolean foreign, Travellers travellers);
    void updateTraveller(int travelerId, Travellers travellers);
    List<Travellers> findAllTravellers();
    Optional<Travellers> findById(int travellerId);
    Optional<Travellers> findByVoyage(String voyageNo);
    Optional<Travellers> findByBus(String plateNumber);
}
