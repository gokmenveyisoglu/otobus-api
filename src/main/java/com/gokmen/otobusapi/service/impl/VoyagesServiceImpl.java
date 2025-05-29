package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.RouteRepository;
import com.gokmen.otobusapi.repository.StationRepository;
import com.gokmen.otobusapi.repository.VoyagesRepository;
import com.gokmen.otobusapi.repository.entities.Voyages;
import com.gokmen.otobusapi.service.VoyagesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoyagesServiceImpl implements VoyagesService {

    VoyagesRepository voyagesRepository;
    RouteRepository routeRepository;
    StationRepository stationRepository;

    public VoyagesServiceImpl(VoyagesRepository voyagesRepository, RouteRepository routeRepository, StationRepository stationRepository){
        this.voyagesRepository = voyagesRepository;
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public void setVoyage(String routeNo, int firstStation, int lastStation, Voyages voyages) {
        routeRepository.findByNo(routeNo).ifPresent(route -> {
            if (voyagesRepository.findByNo(voyages.getVoyageNo()).isEmpty() && routeRepository.findByNo(routeNo).isPresent()) {
                /*if (route.getStations().stream().anyMatch(stations -> voyages.getFirstStation() > stations.getStationOrder() || voyages.getLastStation() > stations.getStationOrder())) {
                    voyages.setFirstStation(firstStation);
                    voyages.setLastStation(lastStation);
                    List<Voyages> voyagesList = new ArrayList<>();
                    voyagesList.add(voyages);
                    route.setVoyages(voyagesList);
                    voyages.setRoutes(route);
                    voyagesRepository.save(voyages);
                }*/
                voyages.setFirstStation(firstStation);
                voyages.setLastStation(lastStation);
                List<Voyages> voyagesList = new ArrayList<>();
                voyagesList.add(voyages);
                route.setVoyages(voyagesList);
                voyages.setRoutes(route);
                voyagesRepository.save(voyages);
            }else System.out.println("Voyage already exist or route doesn't exist"); //Exaption ekle
        });
    }

    @Override
    public void updateVoyage(String voyageNo, Voyages voyages) {
        voyagesRepository.findByNo(voyageNo).ifPresent(voyages1 -> { //Trevalers günceleniyor mu diye bak
            voyages1.setVoyageNo(voyages.getVoyageNo());
            voyages1.setVoyageName(voyages.getVoyageName());
            voyages1.setFirstStation(voyages.getFirstStation());
            voyages1.setLastStation(voyages.getLastStation());
            voyages1.setStartDate(voyages.getStartDate());
            voyages1.setEndDate(voyages.getEndDate());
            voyages1.setVoyagePrice(voyages.getVoyagePrice());
            voyagesRepository.save(voyages1);
        });
    }

    @Override
    public List<Voyages> findAllVoyages() {
        return voyagesRepository.findAll();
    }

    @Override
    public Optional<Voyages> findByNo(String voyageNo) {
        if (voyagesRepository.findByNo(voyageNo).isPresent())
            return voyagesRepository.findByNo(voyageNo);
        else
            return Optional.empty();
    }
}
