package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Travellers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravellerRepository extends JpaRepository<Travellers, Integer> {
}
