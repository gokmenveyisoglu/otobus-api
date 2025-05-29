package com.gokmen.otobusapi.controller;


import com.gokmen.otobusapi.repository.entities.Stations;
import com.gokmen.otobusapi.service.StationsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("station")
@Tag(name = "Stations", description = "İstasyonlar için")
public class StationsController {

    private final StationsService stationsService;

    StationsController(StationsService stationsService){
        this.stationsService = stationsService;
    }

    @PostMapping()
    public void setStation(@RequestBody Stations station){
        stationsService.setStation(station);
    }
    @PutMapping("{sname}")
    public void updateStation(@PathVariable("sname") String name, @RequestBody Stations stations){
        stationsService.updateStation(name, stations);
    }
    @GetMapping()
    public List<Stations> findLists(){
        return stationsService.findStations();
    }




}
