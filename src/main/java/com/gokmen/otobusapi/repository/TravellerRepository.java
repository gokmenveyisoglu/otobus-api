package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Travellers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TravellerRepository extends JpaRepository<Travellers, Integer> {

    @Query("SELECT t FROM Travellers t WHERE t.active = true AND t.busId.bus_id = :busId AND t.voyageId.journeyNo = :journeyNo")
    List<Travellers> findActiveTravellersForJourney(@Param("busId") int busId, @Param("journeyNo") String journeyNo);

}
