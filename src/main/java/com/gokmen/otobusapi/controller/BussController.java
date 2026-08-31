package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Buss;
import com.gokmen.otobusapi.repository.record.Bus.CreateBusRequest;
import com.gokmen.otobusapi.repository.record.Bus.ResponseBus;
import com.gokmen.otobusapi.repository.record.Bus.UpdateBus;
import com.gokmen.otobusapi.service.BussService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("buss")
@Tag(name = "Buss", description = "Otobüs için kulanıllır")
public class BussController {
    private final BussService bussService;

    BussController(BussService bussService){
        this.bussService = bussService;
    }
    @PostMapping()
    public ResponseBus saveBuss(@RequestBody CreateBusRequest request){
        return bussService.saveBuss(request);
    }
    @GetMapping()
    public List<ResponseBus> findAllBuss(){
        return bussService.findAllBuss().stream().map(Buss::toResponse).toList();
    }
    @GetMapping("plate-number/{plate-number}")
    public Optional<Buss> findByPlateNumber(@PathVariable("plate-number") String plateNumber){
        return bussService.findByPlateNumber(plateNumber);
    }
    @PutMapping("{bid}")
    public ResponseBus updateBusSeatsAuto(@PathVariable("bid") int busId, @RequestBody UpdateBus bus){
        return bussService.updateBus(busId, bus);
    }
    @PatchMapping("{busid}/deactivate")
    public ResponseBus deactivateBusById(@PathVariable("busid") int busId) {
        return bussService.deactivateBusById(busId);
    }

}
