package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Buss;
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
    @PostMapping("header/{hid}/route/{rno}")
    public void saveBuss(@PathVariable("hid") int header_id,@PathVariable("rno") String routeNo, @RequestBody Buss buss){
        bussService.saveBuss(header_id, routeNo, buss);
    }
    @GetMapping()
    public List<Buss> findAllBuss(){
        return bussService.findAllBuss();
    }
    @GetMapping("plate-number/{plate-number}")
    public Optional<Buss> findByPlateNumber(@PathVariable("plate-number") String plateNumber){
        return bussService.findByPlateNumber(plateNumber);
    }
    @PutMapping("update-bus-seats-auto/{plate-number}")
    public void updateBusSeatsAuto(@PathVariable("plate-number") String plateNumber){
        bussService.updateBusSeatsAuto(plateNumber);
    }
}
