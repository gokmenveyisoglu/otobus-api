package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.service.RouteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("route")
@Tag(name = "Routes", description = "Rotalar")
public class RouteController {

    RouteService routeService;

    public RouteController(RouteService routeService){
        this.routeService = routeService;
    }

    @PostMapping()
    public void setRoute(@RequestBody Route route){
        routeService.setRoute(route);
    }

    @PutMapping("{rid}/station/{sid}")
    public void addStations(@PathVariable("rid") int routeId, @PathVariable("sid") int stationId){
        routeService.addStation(routeId, stationId);
    }
                                //Alt tarafı kontrol et

    @PutMapping("{rname}")
    public void updateRouteByName(@PathVariable("rname") String routeName, @RequestBody Route route){
        routeService.updateRouteByName(routeName, route);
    }

    @PutMapping("/id/{rid}")
    public void updateRouteById(@PathVariable("rid") int roureId, @RequestBody Route route) {
        routeService.updateRouteById(roureId, route);
    }

    @PatchMapping("/{rid}/deactivate")
    public void deactivateRouteById(@PathVariable("rid") int routeId) {
        routeService.deactivateRouteById(routeId); //Look if it's wrong to send a route for deactivation. I prefer to not send a route object.
    }

    @GetMapping()
    public List<Route> getAllRoutes(){
        return routeService.findAllRoutes();
    }
    @GetMapping("{rname}")
    public Optional<Route> getRouteByName(@PathVariable("rname") String routeName){
        return routeService.findRouteByName(routeName);
    }
}
