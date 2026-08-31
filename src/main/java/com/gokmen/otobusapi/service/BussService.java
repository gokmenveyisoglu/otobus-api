package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Buss;
import com.gokmen.otobusapi.repository.record.Bus.CreateBusRequest;
import com.gokmen.otobusapi.repository.record.Bus.ResponseBus;
import com.gokmen.otobusapi.repository.record.Bus.UpdateBus;

import java.util.List;
import java.util.Optional;

public interface BussService {

    ResponseBus saveBuss(CreateBusRequest request);
    ResponseBus updateBus(int busId, UpdateBus bus);
    ResponseBus deactivateBusById(int busId);
    List<Buss> findAllBuss();
    Optional<Buss> findByPlateNumber(String plateNumber);

}
