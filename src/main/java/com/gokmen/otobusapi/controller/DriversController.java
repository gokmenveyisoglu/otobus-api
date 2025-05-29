package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Drivers;
import com.gokmen.otobusapi.service.DriverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("driver")
@Tag(name = "Driver", description = "Sürüçüler için kullanılır")
public class DriversController {

    DriverService driverService;

    public DriversController(DriverService driverService){
        this.driverService = driverService;
    }
    @PostMapping("{bid}")
    public void saveDriver(@PathVariable("bid") int bus_id, @RequestBody Drivers drivers){
        driverService.saveDriver(bus_id, drivers);
    }
    @PutMapping("{name}")
    public void updateDriverByName(@PathVariable("name") String name, @RequestBody Drivers drivers){
        driverService.updateDriverByName(name, drivers);
    }
    @GetMapping()
    public List<Drivers> getDrivers(){
        return driverService.findDrivers();
    }
    @GetMapping("{name}")
    public Optional<Drivers> getDriverByName(@PathVariable("name") String name){
        return driverService.findDriverByName(name);
    }

}
