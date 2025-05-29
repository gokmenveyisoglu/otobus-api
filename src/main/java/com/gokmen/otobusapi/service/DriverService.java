package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Drivers;

import java.util.List;
import java.util.Optional;

public interface DriverService {

    void saveDriver(int bus_id,Drivers drivers);
    void updateDriverByName(String name, Drivers drivers);
    List<Drivers> findDrivers();
    Optional<Drivers> findDriverByName(String name);

}
