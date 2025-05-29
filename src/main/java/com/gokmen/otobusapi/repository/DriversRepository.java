package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Drivers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DriversRepository  extends JpaRepository<Drivers, Integer> {

    @Query("SELECT d FROM Drivers d WHERE ?1 = d.driverName")
    Optional<Drivers> findByName(String name);
}
