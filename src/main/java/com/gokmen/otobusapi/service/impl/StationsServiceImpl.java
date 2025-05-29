package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.StationRepository;
import com.gokmen.otobusapi.repository.entities.Stations;
import com.gokmen.otobusapi.service.StationsService;
import org.springframework.stereotype.Service;

import java.util.List;

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
            stations1.setStation_name(stations.getStation_name());
            stations1.setAddress(stations.getAddress());
            stations1.setStationOrder(stations.getStationOrder());
            stationRepository.save(stations1);
        });
    }

    @Override
    public List<Stations> findStations() {
        return stationRepository.findAll();
    }
}
