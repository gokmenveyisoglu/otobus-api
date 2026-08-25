package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.KatagoryRepository;
import com.gokmen.otobusapi.repository.UrunRepository;
import com.gokmen.otobusapi.repository.entities.Urun;
import com.gokmen.otobusapi.service.UrunService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UrunServiceImpl implements UrunService {

    UrunRepository urunRepository;
    KatagoryRepository katagoryRepository;

    public UrunServiceImpl(UrunRepository urunRepository, KatagoryRepository katagoryRepository) {
        this.urunRepository = urunRepository;
        this.katagoryRepository = katagoryRepository;
    }

    @Override
    public void setUrun(Urun urun) {

        katagoryRepository.findByType(urun.getKatagories().getType()).ifPresent(katagory -> {
            if (katagory.getUrun().stream().noneMatch(x -> x.getId() == urun.getId())) {
                urun.setKatagories(katagory);
            }
        });

        urunRepository.save(urun);
    }

    @Override
    public List<Urun> findAllUruns() {
        return urunRepository.findAll();
    }

    @Override
    public Urun findUrunByName(String name) {
        return urunRepository.findUrunByName(name);
    }

    @Override
    public Optional<Urun> findUrunByKatagory(String katagory) {
        return urunRepository.findUrunByKatagories(katagory);
    }

}
