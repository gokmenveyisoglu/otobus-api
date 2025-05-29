package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;

import java.util.List;
import java.util.Optional;

public interface SchemaDetailService {

    void saveSchemaDetail(int headerId, List<SchemaDetail> schemaDetail);
    Optional<SchemaDetail> findById(int id);
    List<SchemaDetail> findAll();
    void updateSchemaDetail(int detailId, SchemaDetail schemaDetail);

}
