package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.RouteRepository;
import com.gokmen.otobusapi.repository.StationRepository;
import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.entities.Stations;
import com.gokmen.otobusapi.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class RouteServiceImpl implements RouteService {

    RouteRepository routeRepository;
    StationRepository stationRepository;

    public RouteServiceImpl(RouteRepository routeRepository, StationRepository stationRepository){
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public void setRoute(Route route) {
        if (routeRepository.findByNo(route.getRouteNo()).isEmpty() && route.getStartDate() == route.getEndDate()) {
            route.getStations().forEach(stations -> {
                stations.setRoute(Stream.of(route).toList());
            });
            routeRepository.save(route);
        }
    }

    @Override
    public void addStation(int routeId, int station) {
        routeRepository.findById(routeId).ifPresent(route -> {
            if (true){                                                                  //Koşul bul
                stationRepository.findById(station).ifPresent(stations -> {
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
