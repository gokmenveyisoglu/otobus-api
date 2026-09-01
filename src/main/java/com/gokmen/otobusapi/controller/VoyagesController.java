package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Voyages;
import com.gokmen.otobusapi.repository.record.voyages.CreateVoyage;
import com.gokmen.otobusapi.repository.record.voyages.ResponseVoyage;
import com.gokmen.otobusapi.repository.record.voyages.UpdateVoyage;
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
    public void setVoyage(@RequestBody CreateVoyage request){
        voyagesService.setVoyage(request);
    }

    @PutMapping("{id}")
    public void updateVoyage(@PathVariable("id") int voyageId, @RequestBody UpdateVoyage voyage){
        voyagesService.updateVoyage(voyageId, voyage);
    }
    @PatchMapping("{vid}/deactivate")
    public void deactivateVoyage(@PathVariable("vid") int voyageId) {
        voyagesService.deactivateVoyage(voyageId);
    }
    @GetMapping()
    public List<ResponseVoyage> getAllVoyages(){
        return voyagesService.findAllVoyages();
    }
    @GetMapping("{no}")
    public Optional<Voyages> getByNo(@PathVariable("no") String voyageNo){
        return voyagesService.findByNo(voyageNo);
    }

}
