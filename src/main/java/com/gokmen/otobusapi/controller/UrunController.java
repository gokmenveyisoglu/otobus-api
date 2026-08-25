package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.Urun;
import com.gokmen.otobusapi.service.UrunService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/urun")
@Tag(name = "Ürün", description = "Test için ürün eklenmiştir ve kaldırılmalıdır")
public class UrunController {

    private final UrunService urunService;

    UrunController(UrunService urunService) {
        this.urunService = urunService;
    }

    @PostMapping
    public void addUrun(@RequestBody Urun urun){
        urunService.setUrun(urun);
    }

    @GetMapping
    public List<Urun> findAllUrun() {
        return urunService.findAllUruns();
    }

    @GetMapping("{uname}")
    public Urun findUrunByName(@PathVariable("uname") String uName) {
        return urunService.findUrunByName(uName);
    }

    @GetMapping("{ukatagory}")
    public Optional<Urun> findUrunByKatagory(@PathVariable("ukatagory") String ukatagory) {
        return urunService.findUrunByKatagory(ukatagory);
    }

}
