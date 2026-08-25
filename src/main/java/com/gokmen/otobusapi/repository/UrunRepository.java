package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Urun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrunRepository extends JpaRepository<Urun, Integer> {

    @Query("SELECT u from Urun u where ?1 = u.urunAdi")
    Urun findUrunByName(String urunName);

    @Query("SELECT u FROM Urun u where ?1 = u.katagories.type")
    Optional<Urun> findUrunByKatagories(String katagory);
}
