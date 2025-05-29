package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Stations;

import java.util.List;

public interface StationsService {

    void setStation(Stations station);
    void updateStation(String name, Stations stations);
    List<Stations> findStations();
}
