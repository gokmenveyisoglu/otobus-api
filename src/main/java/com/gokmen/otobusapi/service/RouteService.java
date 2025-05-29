package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    void setRoute(Route route);
    void addStation(int routeId, int station);
    void updateRouteByName(String routeName, Route route);
    List<Route> findAllRoutes();
    Optional<Route> findRouteByName(String routeName);

}
