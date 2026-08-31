package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Voyages;
import com.gokmen.otobusapi.repository.record.Voyage.ResponseVoyage;

import java.util.List;
import java.util.Optional;


public interface VoyagesService {

    ResponseVoyage setVoyage(Voyages voyages);
    void updateVoyage(String voyageNo, Voyages voyages);
    void deactivateVoyage(int voyageId);
    List<Voyages> findAllVoyages();
    Optional<Voyages> findByNo(String voyageNo);
}
