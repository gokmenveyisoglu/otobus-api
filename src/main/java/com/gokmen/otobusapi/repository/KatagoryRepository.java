package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Katagory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KatagoryRepository extends JpaRepository<Katagory, Integer> {

    @Query("SELECT k FROM Katagory k WHERE ?1 = k.type")
    Optional<Katagory> findByType(String type);
}
