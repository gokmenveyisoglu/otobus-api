package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.Katagory;

import java.util.List;

public interface KatagoryService {

    void setKatagory(Katagory katagory);
    List<Katagory> findAllKatagory();
}
