package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Voyages;
import com.gokmen.otobusapi.repository.record.voyages.CreateVoyage;
import com.gokmen.otobusapi.repository.record.voyages.ResponseVoyage;
import com.gokmen.otobusapi.repository.record.voyages.UpdateVoyage;

import java.util.List;
import java.util.Optional;


public interface VoyagesService {

    void setVoyage(CreateVoyage request);
    void updateVoyage(int voyageId, UpdateVoyage voyage);
    void deactivateVoyage(int voyageId);
    List<ResponseVoyage> findAllVoyages();
    Optional<Voyages> findByNo(String voyageNo);
    ResponseVoyage toResponse(Voyages voyages);
}
