package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.BussRepository;
import com.gokmen.otobusapi.repository.RouteRepository;
import com.gokmen.otobusapi.repository.SchemaHeaderRepository;
import com.gokmen.otobusapi.repository.entities.Buss;
import com.gokmen.otobusapi.service.BussService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class BussSerciveImpl implements BussService {

    BussRepository bussRepository;
    SchemaHeaderRepository schemaHeaderRepository;
    RouteRepository routeRepository;

    public BussSerciveImpl(BussRepository bussRepository, SchemaHeaderRepository schemaHeaderRepository, RouteRepository routeRepository) {
        this.bussRepository = bussRepository;
        this.schemaHeaderRepository = schemaHeaderRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public void saveBuss(int headerId, String routeNo, Buss buss) {
        schemaHeaderRepository.findById(headerId).ifPresent(schemaHeader -> {
            if (schemaHeader.getBuss().stream().noneMatch(x -> x.getNumber_plate().equals(buss.getNumber_plate()))) {
                buss.setSchemaHeader(schemaHeader);
                if (schemaHeaderRepository.findById(headerId).isPresent()){
                    buss.getSchemaHeader().getSchemaDetails().forEach(schemaDetail -> {
                        if (schemaDetail.getColumn1() != 0)
                            buss.increaseMaxTraveller();
                        if (schemaDetail.getColumn2() != 0)
                            buss.increaseMaxTraveller();
                        if (schemaDetail.getColumn3() != 0)
                            buss.increaseMaxTraveller();
                        if (schemaDetail.getColumn4() != 0)
                            buss.increaseMaxTraveller();
                        if (schemaDetail.getColumn5() != 0)
                            buss.increaseMaxTraveller();

                    });
                }
                routeRepository.findByNo(routeNo).ifPresent(route -> {
                    if(route.getRouteNo().equals(routeNo)){                                                                                           //Koşul bul
                        route.setBuss(Stream.of(buss).toList());
                        buss.setRoute(route);
                        bussRepository.save(buss);
                    }
                });
            }
        });
    }

    @Override
    public void updateBusSeatsAuto(String plateNumber) {
        bussRepository.findByPlateNumber(plateNumber).ifPresent(buss -> {
            buss.setMax_traveller(0);
            buss.getSchemaHeader().getSchemaDetails().forEach(schemaDetail -> {
                if (schemaDetail.getColumn1() != 0)
                    buss.increaseMaxTraveller();
                if (schemaDetail.getColumn2() != 0)
                    buss.increaseMaxTraveller();
                if (schemaDetail.getColumn3() != 0)
                    buss.increaseMaxTraveller();
                if (schemaDetail.getColumn4() != 0)
                    buss.increaseMaxTraveller();
                if (schemaDetail.getColumn5() != 0)
                    buss.increaseMaxTraveller();

            });
            bussRepository.save(buss);
        });
    }

    @Override
    public List<Buss> findAllBuss() {
        return bussRepository.findAll();
    }

    @Override
    public Optional<Buss> findByPlateNumber(String plateNumber) {
        if (bussRepository.findByPlateNumber(plateNumber).isPresent())
            return bussRepository.findByPlateNumber(plateNumber);
        else
            return Optional.empty();
    }
}
