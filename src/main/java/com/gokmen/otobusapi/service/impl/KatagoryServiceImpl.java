package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.KatagoryRepository;
import com.gokmen.otobusapi.repository.entities.Katagory;
import com.gokmen.otobusapi.service.KatagoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KatagoryServiceImpl implements KatagoryService {

    KatagoryRepository katagoryRepository;

    public KatagoryServiceImpl(KatagoryRepository katagoryRepository) {
        this.katagoryRepository = katagoryRepository;
    }

    @Override
    public void setKatagory(Katagory katagory) {
        katagoryRepository.save(katagory);
    }

    @Override
    public List<Katagory> findAllKatagory() {
        return katagoryRepository.findAll();
    }
}
