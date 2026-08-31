package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Voyages;
import com.gokmen.otobusapi.repository.record.Voyage.ResponseVoyage;
import com.gokmen.otobusapi.service.VoyagesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("voyages")
@Tag(name = "Voyages",description = "Seferler için kulanılır.")
public class VoyagesController {


    private final VoyagesService voyagesService;

    VoyagesController(VoyagesService voyagesService){
        this.voyagesService = voyagesService;
    }

    @PostMapping()
    public ResponseVoyage setVoyage(@RequestBody Voyages voyages){
        return voyagesService.setVoyage(voyages);
    }

    @PutMapping("{no}")
    public void updateVoyage(@PathVariable("no") String voyageNo, @RequestBody Voyages voyages){
        voyagesService.updateVoyage(voyageNo, voyages);
    }
    @PatchMapping("{voyageid}/deactivate")
    public void deactivateVoyage(@PathVariable("voyageid") int voyageId) {
        voyagesService.deactivateVoyage(voyageId);
    }
    @GetMapping()
    public List<Voyages> getAllVoyages(){
        return voyagesService.findAllVoyages();
    }
    @GetMapping("{no}")
    public Optional<Voyages> getByNo(@PathVariable("no") String voyageNo){
        return voyagesService.findByNo(voyageNo);
    }

}
