package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SchemaHeaderRepository extends JpaRepository<SchemaHeader, Integer> {

    @Query("SELECT s FROM SchemaHeader s WHERE ?1 = s.name")
    Optional<SchemaHeader> findByName(String name);

    @Query("SELECT s.schemaDetails FROM SchemaHeader s WHERE ?1 = s.name")
    Optional<SchemaDetail> findDetails(String name);

}
