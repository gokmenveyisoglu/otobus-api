package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Stations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Stations, Integer> {

    @Query("SELECT s FROM Stations s WHERE ?1 = s.stationName")
    Optional<Stations> findByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Stations s where s.stationName = :name")
    void deleteByName(@Param("name") String name);

   /* @Query("SELECT s FROM Stations s WHERE ?1 = s.stationOrder AND ?2 = s.route")
    Optional<Stations> findByExo(int Order, Route route);*/
}
