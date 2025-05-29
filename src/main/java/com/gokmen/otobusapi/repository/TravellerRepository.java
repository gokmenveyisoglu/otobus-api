package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Travellers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TravellerRepository extends JpaRepository<Travellers, Integer> {

    @Query("SELECT t FROM Travellers t WHERE ?1 = t.voyageNo.voyageNo")
    Optional<Travellers> findAllByVoyage(String voyageNo);

}
