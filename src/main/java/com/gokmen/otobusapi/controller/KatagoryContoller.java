package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Katagory;
import com.gokmen.otobusapi.service.KatagoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("katagory")
@Tag(name = "Katagor", description = "Test amaçlı")
public class KatagoryContoller {

    KatagoryService katagoryService;

    public KatagoryContoller(KatagoryService katagoryService) {
        this.katagoryService = katagoryService;
    }

    @PostMapping()
    void addKatagory(@RequestBody Katagory katagory) {
        katagoryService.setKatagory(katagory);
    }

    @GetMapping
    List<Katagory> findAllKatagory() {
        return katagoryService.findAllKatagory();
    }
}
