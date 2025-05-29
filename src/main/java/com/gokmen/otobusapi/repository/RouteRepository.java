package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    @Query("SELECT r FROM Route r WHERE ?1 = r.RouteNo")
    Optional<Route> findByNo(String routeNo);

}
