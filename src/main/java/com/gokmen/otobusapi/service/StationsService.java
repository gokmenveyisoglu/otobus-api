package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Stations;

import java.util.List;

public interface StationsService {

    void setStation(Stations station);
    void updateStation(String name, Stations stations);
    void updateStationById(int id, Stations stations);
    List<Stations> findStations();
    void deleteStation(String name);
    void deleteStationById(int id);
}
