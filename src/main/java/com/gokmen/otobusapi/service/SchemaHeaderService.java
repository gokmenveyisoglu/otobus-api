package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;

import java.util.List;
import java.util.Optional;

public interface SchemaHeaderService {

    void saveSchemaHeader(SchemaHeader schemaHeader);
    void updateSchemaHeader(int headerId, SchemaHeader schemaHeader);
    List<SchemaHeader> findAll();
    Optional<SchemaHeader> getById(int id);
    Optional<SchemaHeader> findByName(String name);
    Optional<SchemaDetail> findDetails(String name);



}
