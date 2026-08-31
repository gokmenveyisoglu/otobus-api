package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaHeader.CreateSchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.ResponseSchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.UpdateSchemaHeader;

import java.util.List;
import java.util.Optional;

public interface SchemaHeaderService {

//    void saveSchemaHeader(SchemaHeader schemaHeader);
    ResponseSchemaHeader saveSchemaHeader(CreateSchemaHeader request);
    ResponseSchemaHeader updateSchemaHeader(int headerId, UpdateSchemaHeader request);
    ResponseSchemaHeader deactivateById(int headerId);
    List<ResponseSchemaHeader> findAll();
    ResponseSchemaHeader getById(int id);
    ResponseSchemaHeader findByName(String name);
    Optional<SchemaDetail> findDetails(String name);



}
