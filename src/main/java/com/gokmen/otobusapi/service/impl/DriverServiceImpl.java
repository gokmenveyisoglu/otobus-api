package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.BussRepository;
import com.gokmen.otobusapi.repository.DriversRepository;
import com.gokmen.otobusapi.repository.entities.Drivers;
import com.gokmen.otobusapi.service.DriverService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class DriverServiceImpl implements DriverService {

    DriversRepository driversRepository;
    BussRepository bussRepository;

    public DriverServiceImpl(DriversRepository driversRepository, BussRepository bussRepository) {
        this.driversRepository = driversRepository;
        this.bussRepository = bussRepository;
    }

    @Override
    public void saveDriver(int bus_id, Drivers drivers) {
        bussRepository.findById(bus_id).ifPresent(bus -> {
            if (bus.getDriverId().stream().noneMatch(x -> x.getDriverName().equals(drivers.getDriverName()))) {
                drivers.setBusId(Stream.of(bus).toList());
                drivers.setBusNo(bus_id);
                driversRepository.save(drivers);
            }
        });
    }

    @Override
    public void updateDriverByName(String name, Drivers drivers) {
        driversRepository.findByName(name).ifPresent(drivers1 -> {
            drivers1.setDriverName(drivers.getDriverName());
            drivers1.setDriverSurname(drivers.getDriverSurname());
            drivers.getBusId().forEach(buss -> {
                List<Drivers> driversList = new ArrayList<>();
                driversList.add(drivers);
                buss.setDriverId(driversList);
            });
            drivers1.setBusId(drivers.getBusId());
            drivers1.setBusNo(drivers.getBusNo());
            driversRepository.save(drivers1);

        });
    }

    @Override
    public List<Drivers> findDrivers() {
        return driversRepository.findAll();
    }

    @Override
    public Optional<Drivers> findDriverByName(String name) {
        if (driversRepository.findByName(name).isPresent())
            return driversRepository.findByName(name);
        else return Optional.empty();
    }
}
