package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.BussRepository;
import com.gokmen.otobusapi.repository.RouteRepository;
import com.gokmen.otobusapi.repository.StationRepository;
import com.gokmen.otobusapi.repository.VoyagesRepository;
import com.gokmen.otobusapi.repository.entities.Buss;
import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.entities.Stations;
import com.gokmen.otobusapi.repository.entities.Voyages;
import com.gokmen.otobusapi.repository.record.voyages.CreateVoyage;
import com.gokmen.otobusapi.repository.record.voyages.ResponseVoyage;
import com.gokmen.otobusapi.repository.record.voyages.UpdateVoyage;
import com.gokmen.otobusapi.service.VoyagesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoyagesServiceImpl implements VoyagesService {

    VoyagesRepository voyagesRepository;
    RouteRepository routeRepository;
    BussRepository bussRepository;
    StationRepository stationRepository;

    public VoyagesServiceImpl(VoyagesRepository voyagesRepository, RouteRepository routeRepository, BussRepository bussRepository, StationRepository stationRepository){
        this.voyagesRepository = voyagesRepository;
        this.routeRepository = routeRepository;
        this.bussRepository = bussRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public void setVoyage(CreateVoyage request) {

        Route route = routeRepository.findById(request.routeId()).orElseThrow(() -> new RuntimeException("Rout not found " + request.routeId()));

        Buss bus = bussRepository.findById(request.busId()).orElseThrow(() -> new RuntimeException("Bus not found " + request.busId()));

        if (!route.isActive())
            throw new RuntimeException("Selected route is inactive");
        if (!bus.isActive())
            throw new RuntimeException("Selected bus is inactive");

        if (bus.getRoute() == null || bus.getRoute().getId() != route.getId())
            throw new RuntimeException("Selected bus does not belong to selected route");

        List<Stations> orderedStations = route.getStations();

        int firstStation = findStationIndex(orderedStations, request.departureStationId());
        int lastStation = findStationIndex(orderedStations, request.arrivalStationId());

        if (firstStation == -1)
            throw new RuntimeException("Departure station is not part of the selected route");

        if (lastStation == -1)
            throw new RuntimeException("Arrival station is not part of the selected route");

        if (firstStation == lastStation)
            throw new RuntimeException("Departure station and arrival station cannot be same");

        if (request.startDate() == null || request.endDate() == null || (!request.startDate().before(request.endDate()) && !request.startDate().equals(request.endDate())))
            throw new RuntimeException("Voyage end date must be after its start date");

        Voyages voyages = new Voyages();

        voyages.setVoyageNo(request.voyageNo());
        voyages.setJourneyNo(request.journeyNo());
        voyages.setVoyageName(request.voyageName());
        voyages.setVoyagePrice(request.voyagePrice());
        voyages.setRoutes(route);
        voyages.setBus(bus);
        voyages.setFirstStation(firstStation);
        voyages.setLastStation(lastStation);
        voyages.setStartDate(request.startDate());
        voyages.setEndDate(request.endDate());
        voyages.setActive(true);

        voyagesRepository.save(voyages);


        /*routeRepository.findByNo(routeNo).ifPresent(route -> {
            if (voyagesRepository.findByNo(voyages.getVoyageNo()).isEmpty() && routeRepository.findByNo(routeNo).isPresent()) {
                voyages.setFirstStation(firstStation);
                voyages.setLastStation(lastStation);
                List<Voyages> voyagesList = new ArrayList<>();
                voyagesList.add(voyages);
                route.setVoyages(voyagesList);
                voyages.setRoutes(route);
                voyagesRepository.save(voyages);
            }else System.out.println("Voyage already exist or route doesn't exist"); //Exaption ekle
        });*/
    }

    @Override
    public void updateVoyage(int voyageId, UpdateVoyage voyage) {
        Route route = routeRepository.findById(voyage.routeId()).orElseThrow(() -> new RuntimeException("Route not found " + voyage.routeId()));
        Buss bus = bussRepository.findById(voyage.busId()).orElseThrow(() -> new RuntimeException("Bus not found " + voyage.busId()));

        if (!route.isActive())
            throw new RuntimeException("Selected route is inactive");
        if (!bus.isActive())
            throw new RuntimeException("Selected bus is inactive");

        if (bus.getRoute() == null || bus.getRoute().getId() != route.getId())
            throw new RuntimeException("Selected bus does not belong to selected route");

        List<Stations> orderedStations = route.getStations();

        int firstStation = findStationIndex(orderedStations, voyage.departureStationId());
        int lastStation = findStationIndex(orderedStations, voyage.arrivalStationId());

        if (firstStation == -1)
            throw new RuntimeException("Departure station is not part of the selected route");

        if (lastStation == -1)
            throw new RuntimeException("Arrival station is not part of the selected route");

        if (firstStation == lastStation)
            throw new RuntimeException("Departure station and arrival station cannot be same");

        if (voyage.startDate() == null || voyage.endDate() == null || (!voyage.startDate().before(voyage.endDate()) && !voyage.startDate().equals(voyage.endDate())))
            throw new RuntimeException("Voyage end date must be after its start date");

        voyagesRepository.findById(voyageId).ifPresent(voyages -> {
            voyages.setVoyageNo(voyage.voyageNo());
            voyages.setJourneyNo(voyage.journeyNo());
            voyages.setVoyageName(voyage.voyageName());
            voyages.setVoyagePrice(voyage.voyagePrice());
            voyages.setRoutes(route);
            voyages.setBus(bus);
            voyages.setFirstStation(firstStation);
            voyages.setLastStation(lastStation);
            voyages.setStartDate(voyage.startDate());
            voyages.setEndDate(voyage.endDate());
            voyagesRepository.save(voyages);
        });
    }

    @Override
    @Transactional
    public void deactivateVoyage(int voyageId) {
        voyagesRepository.findById(voyageId).ifPresent(voyages -> {
            voyages.setActive(false);
            voyagesRepository.save(voyages);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseVoyage> findAllVoyages() {
        return voyagesRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public Optional<Voyages> findByNo(String voyageNo) {
        if (voyagesRepository.findByNo(voyageNo).isPresent())
            return voyagesRepository.findByNo(voyageNo);
        else
            return Optional.empty();
    }


    private int findStationIndex(List<Stations> stations, int stationId) {
        for (int index = 0; index < stations.size(); index++) {
            if (stations.get(index).getStationId() == stationId)
                return index;
        }
        return -1;
    }

    @Override
    public ResponseVoyage toResponse(Voyages voyages) {
        List<Stations> orderStations = voyages.getRoutes().getStations();

        int firstStationIndex = voyages.getFirstStation();
        int lastStationIndex = voyages.getLastStation();

        if (firstStationIndex < 0 || firstStationIndex >= orderStations.size())
            throw new RuntimeException("Voyage contain an invalid departure station position");
        if (lastStationIndex < 0 || lastStationIndex >= orderStations.size())
            throw new RuntimeException("Voyage contain an invalid arrival station position");

        int departureStationId = orderStations.get(firstStationIndex).getStationId();
        int arrivalStationId = orderStations.get(lastStationIndex).getStationId();

        return new ResponseVoyage(
                voyages.getVoyageId(),
                voyages.getVoyageNo(),
                voyages.getJourneyNo(),
                voyages.getVoyageName(),
                voyages.getRoutes(),
                departureStationId,
                arrivalStationId,
                Buss.toResponse(voyages.getBus()),
                voyages.getStartDate(),
                voyages.getEndDate(),
                voyages.getVoyagePrice(),
                voyages.isActive()
        );
    }
}
