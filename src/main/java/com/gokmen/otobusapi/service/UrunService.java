package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Urun;

import java.util.List;
import java.util.Optional;

public interface UrunService {

    void setUrun(Urun urun);
    List<Urun> findAllUruns();
    Urun findUrunByName(String name);
    Optional<Urun> findUrunByKatagory(String katagory);
}
