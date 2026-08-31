package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchemaDetailRepository extends JpaRepository<SchemaDetail, Integer> {

    <S extends SchemaDetail> List<S> findAllBySchemaHeader(SchemaDetail schemaDetail);

    List<SchemaDetail> findAllBySchemaHeader_Id(int headerId);
}
