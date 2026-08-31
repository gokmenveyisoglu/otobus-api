package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.RouteRepository;
import com.gokmen.otobusapi.repository.StationRepository;
import com.gokmen.otobusapi.repository.VoyagesRepository;
import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.entities.Voyages;
import com.gokmen.otobusapi.repository.record.Voyage.ResponseVoyage;
import com.gokmen.otobusapi.service.VoyagesService;
import jakarta.persistence.Transient;
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
    @Transient
    public ResponseVoyage setVoyage(Voyages voyages) {
        Route route = routeRepository.findById(voyages.getRoute().getId()).orElseThrow(() -> new RuntimeException("Route not found" + voyages.getRoute().getId()));

        List<Voyages> voyagesList = new ArrayList<>();
        voyagesList.add(voyages);
        route.setVoyages(voyagesList);
        voyages.setRoute(route);
        Voyages savedVoyage = voyagesRepository.save(voyages);
        return Voyages.toResponse(savedVoyage);


        /*routeRepository.findById(voyages.getRoute().getId()).ifPresent(route -> {
            if (voyagesRepository.findByNo(voyages.getVoyageNo()).isEmpty() && routeRepository.findByNo(voyages.getRoute().getRouteNo()).isPresent()) {
                voyages.setFirstStation(voyages.getFirstStation());
                voyages.setLastStation(voyages.getLastStation());
                List<Voyages> voyagesList = new ArrayList<>();
                voyagesList.add(voyages);
                route.setVoyages(voyagesList);
                voyages.setRoute(route);
                voyagesRepository.save(voyages);
            }else System.out.println("Voyage already exist or route doesn't exist"); //Exaption ekle
        });*/
    }

    @Override
    public void updateVoyage(String voyageNo, Voyages voyages) {
        voyagesRepository.findByNo(voyageNo).ifPresent(voyages1 -> { //Trevalers günceleniyor mu diye bak
            voyages1.setVoyageNo(voyages.getVoyageNo());
            voyages1.setVoyageName(voyages.getVoyageName());
            voyages1.setDepartureStation(voyages.getDepartureStation());
            voyages1.setArrivalStation(voyages.getArrivalStation());
            voyages1.setStartDate(voyages.getStartDate());
            voyages1.setEndDate(voyages.getEndDate());
            voyages1.setVoyagePrice(voyages.getVoyagePrice());
            voyagesRepository.save(voyages1);
        }); //Kontrol ed
    }

    @Override
    public void deactivateVoyage(int voyageId) {
        voyagesRepository.findById(voyageId).ifPresent(voyages -> {
            voyages.setActive(false);
            voyagesRepository.save(voyages);
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
