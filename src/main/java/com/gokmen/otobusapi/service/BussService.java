package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Buss;

import java.util.List;
import java.util.Optional;

public interface BussService {

    void saveBuss(int headerId, String routeNo, Buss buss);
    void updateBusSeatsAuto(String plateNumber);
    List<Buss> findAllBuss();
    Optional<Buss> findByPlateNumber(String plateNumber);

}
