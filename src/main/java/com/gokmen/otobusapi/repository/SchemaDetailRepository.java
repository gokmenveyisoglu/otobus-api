package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SchemaDetailRepository extends JpaRepository<SchemaDetail, Integer> {

    <S extends SchemaDetail> List<S> findAllBySchemaHeader(SchemaDetail schemaDetail);
}
