package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.BussRepository;
import com.gokmen.otobusapi.repository.RouteRepository;
import com.gokmen.otobusapi.repository.SchemaHeaderRepository;
import com.gokmen.otobusapi.repository.entities.Buss;
import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;
import com.gokmen.otobusapi.repository.record.Bus.CreateBusRequest;
import com.gokmen.otobusapi.repository.record.Bus.ResponseBus;
import com.gokmen.otobusapi.repository.record.Bus.UpdateBus;
import com.gokmen.otobusapi.service.BussService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    @Transactional
    public ResponseBus saveBuss(CreateBusRequest request) {

        SchemaHeader schemaHeader = schemaHeaderRepository.findById(request.schema_header_id()).orElseThrow(() -> new RuntimeException("Seat layout not found " + request.schema_header_id()));

        Route route = routeRepository.findById(request.route_id()).orElseThrow(() -> new RuntimeException("Route not found " + request.route_id()));

        boolean plateExists = bussRepository.findByPlateNumber(request.number_plate()).isPresent();

        if (plateExists) throw  new RuntimeException("A bus with the same plate number exists");

        Buss buss = new Buss();

        buss.setNumberPlate(request.number_plate().trim());
        buss.setSchemaHeader(schemaHeader);
        buss.setRoute(route);
        buss.setActive(true);

        int capacity = schemaHeader.getSchemaDetails().stream().mapToInt(detail ->
                seatCount(detail.getColumn1()) + seatCount(detail.getColumn2()) + seatCount(detail.getColumn4()) + seatCount(detail.getColumn5())).sum();

        buss.setMaxTraveller(capacity);

        Buss savedBuss = bussRepository.save(buss);
        return Buss.toResponse(savedBuss);
    }

    @Override
    @Transactional
    public ResponseBus updateBus(int busId, UpdateBus updateBus) {
        Buss buss = bussRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found" + busId));

        buss.setNumberPlate(updateBus.number_plate());
        buss.setSchemaHeader(schemaHeaderRepository.findById(updateBus.schema_header_id()).orElseThrow(() -> new RuntimeException("Seat Layout nor found" + updateBus.schema_header_id())));
        buss.setRoute(routeRepository.findById(updateBus.route_id()).orElseThrow(() -> new RuntimeException("route not found" + updateBus.route_id())));

        buss.setMaxTraveller(0);
        buss.getSchemaHeader().getSchemaDetails().forEach(schemaDetail -> {
            if (schemaDetail.getColumn1() != 0)
                buss.increaseMaxTraveller();
            if (schemaDetail.getColumn2() != 0)
                buss.increaseMaxTraveller();
            if (schemaDetail.getColumn4() != 0)
                buss.increaseMaxTraveller();
            if (schemaDetail.getColumn5() != 0)
                buss.increaseMaxTraveller();

        });
        Buss savedBus = bussRepository.save(buss);
        return Buss.toResponse(savedBus);
    }

    @Override
    @Transactional
    public ResponseBus deactivateBusById(int busId) {
        Buss buss = bussRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found" + busId));
        buss.setActive(false);
        Buss savedBus = bussRepository.save(buss);
        return Buss.toResponse(savedBus);
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

    private int seatCount(int seatNumber) {
        return seatNumber > 0 ? 1 : 0;
    }
}
