package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Travellers;
import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.ResponseTraveller;
import com.gokmen.otobusapi.repository.record.Traveller.UpdateTraveller;
import com.gokmen.otobusapi.service.BookingService;
import com.gokmen.otobusapi.service.TravellersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("travellers")
@Tag(name = "Travellars", description = "Yolcular için")
public class TravellersController {

    TravellersService travellersService;
    BookingService bookingService;

    public TravellersController(TravellersService travellersService, BookingService bookingService){
        this.travellersService = travellersService;
        this.bookingService = bookingService;
    }

    @PostMapping()
    public void setTraveler(@RequestBody CreateTraveller request){
        travellersService.setTraveller(request);
    }
    @PutMapping("{id}")
    public void updateTraveler(@PathVariable("id") int travelerId, @RequestBody UpdateTraveller request){
        travellersService.updateTraveller(travelerId, request);
    }
    @PatchMapping("{id}/deactivate")
    public void deactivateTraveller(@PathVariable("id") int travellerId) {
        travellersService.deactivateTraveller(travellerId);
    }
    @GetMapping()
    public List<ResponseTraveller> getAllTravellers(){
        return travellersService.findAllTravellers();
    }
    @GetMapping("{id}")
    public Optional<Travellers> getTravellerById(@PathVariable("id") int travellerId){
        return travellersService.findById(travellerId);
    }
    @GetMapping("/occupied-seats/{voyageId}")
    public List<Integer> getOccupiedSeats(@PathVariable("voyageId") int voyageId) {
        return bookingService.getOccupiedSeats(voyageId);
    }

}
