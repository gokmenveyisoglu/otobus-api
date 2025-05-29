package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Voyages;
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

    @PostMapping("route/{rname}/first-station/{forder}/{loder}")
    public void setVoyage(@PathVariable("rname") String routeName, @PathVariable("forder") int firstStation, @PathVariable("loder") int lastStation, @RequestBody Voyages voyages){
        voyagesService.setVoyage(routeName, firstStation, lastStation, voyages);
    }

    @PutMapping("{no}")
    public void updateVoyage(@PathVariable("no") String voyageNo, @RequestBody Voyages voyages){
        voyagesService.updateVoyage(voyageNo, voyages);
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
