package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Voyages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoyagesRepository extends JpaRepository<Voyages,Integer> {

    @Query("SELECT v FROM Voyages v WHERE ?1 = v.voyageNo")
    Optional<Voyages> findByNo(String voyageNo);

}
