package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Travellers;
import com.gokmen.otobusapi.service.TravellersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("travellars")
@Tag(name = "Travellars", description = "Yolcular için")
public class TravellersController {

    TravellersService travellersService;

    public TravellersController(TravellersService travellersService){
        this.travellersService = travellersService;
    }

    @PostMapping("voyage/{vno}/bus/{bno}/seat/{sno}/foreigner/{foreign}")
    public void setTraveler(@PathVariable("vno") String voyageNo,@PathVariable("bno") int busNo, @PathVariable("sno") int seatNo, @PathVariable("foreign") boolean foreign, @RequestBody Travellers travellers){
        travellersService.setTraveller(voyageNo, busNo, seatNo, foreign, travellers);
    }
    @PutMapping("{id}")
    public void updateTraveler(@PathVariable("id") int travelerId, @RequestBody Travellers travellers){
        travellersService.updateTraveller(travelerId, travellers);
    }
    @GetMapping()
    public List<Travellers> getAllTravellers(){
        return travellersService.findAllTravellers();
    }
    @GetMapping("{id}")
    public Optional<Travellers> getTravellerById(@PathVariable("id") int travellerId){
        return travellersService.findById(travellerId);
    }
    @GetMapping("{vno}")
    public Optional<Travellers> getTravellersByVoyage(@PathVariable("vno") String voyageNo){
        return travellersService.findByVoyage(voyageNo);
    }
    @GetMapping("bus/{bus-plate-number}")
    public Optional<Travellers> getTravellersByBus(@PathVariable("bus-plate-number") String busPlateNumber){
        return travellersService.findByBus(busPlateNumber);
    }

}
