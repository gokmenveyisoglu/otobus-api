package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.entities.Stations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Stations, Integer> {

    @Query("SELECT s FROM Stations s WHERE ?1 = s.station_name")
    Optional<Stations> findByName(String name);

   /* @Query("SELECT s FROM Stations s WHERE ?1 = s.stationOrder AND ?2 = s.route")
    Optional<Stations> findByExo(int Order, Route route);*/
}
