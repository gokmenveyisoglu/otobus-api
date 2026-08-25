package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.StationRepository;
import com.gokmen.otobusapi.repository.entities.Stations;
import com.gokmen.otobusapi.service.StationsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationsServiceImpl implements StationsService {

    StationRepository stationRepository;

    public StationsServiceImpl(StationRepository stationRepository){
        this.stationRepository = stationRepository;
    }


    @Override
    public void setStation(Stations station) {
        stationRepository.save(station);
    }

    @Override
    public void updateStation(String name, Stations stations) {
        stationRepository.findByName(name).ifPresent(stations1 -> {
            stations1.setStationName(stations.getStationName());
            stations1.setAddress(stations.getAddress());
            stationRepository.save(stations1);
        });
    }

    @Override
    public void updateStationById(int id, Stations stations) {
        stationRepository.findById(id).ifPresent(stations1 -> {
            stations1.setStationName(stations.getStationName());
            stations1.setAddress(stations.getAddress());
            stationRepository.save(stations1);
        });
    }

    @Override
    public List<Stations> findStations() {
        return stationRepository.findAll();
    }

    @Override
    public void deleteStation(String name) {
        stationRepository.deleteByName(name);
    }

    @Override
    public void deleteStationById(int id) {
        stationRepository.deleteById(id);
    }
}
