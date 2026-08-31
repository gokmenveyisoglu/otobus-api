package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.record.SchemaDetail.CreateSchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaDetail.ResponseSchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaDetail.UpdateSchemaDetail;

import java.util.List;

public interface SchemaDetailService {

    List<ResponseSchemaDetail> saveSchemaDetail(int headerId, List<CreateSchemaDetail> schemaDetail);
    ResponseSchemaDetail findById(int id);
    List<ResponseSchemaDetail> findAll();
    List<ResponseSchemaDetail> findAllByHeaderId(int headerId);
    ResponseSchemaDetail updateSchemaDetail(int detailId, UpdateSchemaDetail schemaDetail);

}
