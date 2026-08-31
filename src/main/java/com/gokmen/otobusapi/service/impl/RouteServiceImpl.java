package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.RouteRepository;
import com.gokmen.otobusapi.repository.StationRepository;
import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.entities.Stations;
import com.gokmen.otobusapi.service.RouteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    RouteRepository routeRepository;
    StationRepository stationRepository;

    public RouteServiceImpl(RouteRepository routeRepository, StationRepository stationRepository){
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    @Transactional
    public void setRoute(Route route) {
        if (routeRepository.findByNo(route.getRouteNo()).isEmpty()) {
            List<Stations> managedStations = route.getStations().stream().map(stations ->
                    stationRepository.findById(stations.getStationId()).orElseThrow(() ->
                            new EntityNotFoundException("Station not found: " + stations.getStationId()))).toList();

            route.setStations(managedStations);

            managedStations.forEach(stations -> {
                if (stations.getRoute() == null) {
                    stations.setRoute(new ArrayList<>());
                }
                stations.getRoute().add(route);
            });
            routeRepository.save(route);
        }
    }

    @Override
    @Transactional
    public void addStation(int routeId, int stationId) {
        routeRepository.findById(routeId).ifPresent(route -> {
            if (true){                                                                  //Koşul bul
                stationRepository.findById(stationId).ifPresent(stations -> {
                    List<Stations> stationsList = new ArrayList<>();
                    stationsList.add(stations);
                    route.setStations(stationsList);
                    List<Route> routeList = new ArrayList<>();
                    routeList.add(route);
                    stations.setRoute(routeList);
                });
                routeRepository.save(route);
            }
        });
    }

    @Override
    public void updateRouteByName(String routeName, Route route) {
        routeRepository.findByNo(routeName).ifPresent(route1 -> {
            route.getStations().forEach(stations -> {
                List<Route> routeList = new ArrayList<>();
                routeList.add(route);
                stations.setRoute(routeList);
            });
            route1.setRouteNo(route.getRouteNo());
            route1.setStartDate(route.getStartDate());
            route1.setEndDate(route.getEndDate());
            route1.setStations(route.getStations());
            route1.setBuss(route.getBuss());
            route1.setVoyages(route.getVoyages());
            routeRepository.save(route1);
        });
    }

    @Override
    @Transactional
    public void updateRouteById(int routeId, Route request) {
        Route existingRoute = routeRepository.findById(routeId).orElseThrow(() -> new EntityNotFoundException("Route not found: " + routeId ));

        List<Stations> managedStations = request.getStations().stream().map(stationReference ->
                stationRepository.findById(stationReference.getStationId()).orElseThrow(() ->
                        new EntityNotFoundException("Station not found: " + stationReference.getStationId()))).collect(Collectors.toCollection(ArrayList::new));

        existingRoute.setRouteNo(request.getRouteNo());
        existingRoute.setStartDate(request.getStartDate());
        existingRoute.setEndDate(request.getEndDate());
        existingRoute.setActive(request.isActive());

        existingRoute.setStations(managedStations);

        routeRepository.save(existingRoute);
    }

    @Override
    @Transactional
    public void deactivateRouteById(int routeId) {
        routeRepository.findById(routeId).ifPresent(route1 -> {
            route1.setActive(false);
            routeRepository.save(route1);
        });
    }

    @Override
    public List<Route> findAllRoutes() {
        return routeRepository.findAll();
    }

    @Override
    public Optional<Route> findRouteByName(String routeName) {
        if (routeRepository.findByNo(routeName).isPresent())
            return routeRepository.findByNo(routeName);
        else
            return Optional.empty();
    }
}
