package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Voyages;

import java.util.List;
import java.util.Optional;


public interface VoyagesService {

    void setVoyage(String routeNo, int firstStation, int lastStation, Voyages voyages);
    void updateVoyage(String voyageNo, Voyages voyages);
    List<Voyages> findAllVoyages();
    Optional<Voyages> findByNo(String voyageNo);
}
