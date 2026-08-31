package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Buss;
import com.gokmen.otobusapi.repository.entities.Travellers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BussRepository extends JpaRepository<Buss, Integer> {

    @Query("SELECT b FROM Buss b WHERE ?1 = b.numberPlate")
    Optional<Buss> findByPlateNumber(String plateNumber);

    @Query("SELECT b.travellers FROM Buss b WHERE ?1 = b.numberPlate")
    Optional<Travellers> findAllTravellersByPlateNumber(String plateNumber);


}
