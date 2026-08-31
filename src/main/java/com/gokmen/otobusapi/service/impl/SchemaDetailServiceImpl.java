package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.SchemaDetailRepository;
import com.gokmen.otobusapi.repository.SchemaHeaderRepository;
import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaDetail.CreateSchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaDetail.ResponseSchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaDetail.UpdateSchemaDetail;
import com.gokmen.otobusapi.service.SchemaDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchemaDetailServiceImpl implements SchemaDetailService {
    private final SchemaDetailRepository schemaDetailRepository;
    private final SchemaHeaderRepository schemaHeaderRepository;

    public SchemaDetailServiceImpl(SchemaDetailRepository schemaDetailRepository, SchemaHeaderRepository schemaHeaderRepository) {
        this.schemaDetailRepository = schemaDetailRepository;
        this.schemaHeaderRepository = schemaHeaderRepository;
    }

    @Override
    @Transactional
    public List<ResponseSchemaDetail> saveSchemaDetail(int headerId, List<CreateSchemaDetail> schemaDetail) {
        SchemaHeader schemaHeader = schemaHeaderRepository.findById(headerId).orElseThrow(() -> new RuntimeException("Seat layout not found: " + headerId));

        List<SchemaDetail> schemaDetails = schemaDetail.stream().map(request -> {
            SchemaDetail detail = SchemaDetail.fromCreate(request);
            detail.setSchemaHeader(schemaHeader);
            return detail;
        }).toList();

        return schemaDetailRepository.saveAll(schemaDetails).stream().map(SchemaDetail::toResponse).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseSchemaDetail findById(int id) {
        SchemaDetail schemaDetail = schemaDetailRepository.findById(id).orElseThrow(() -> new RuntimeException("Seat row not found: " + id));
        return SchemaDetail.toResponse(schemaDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSchemaDetail> findAll() {
        return schemaDetailRepository.findAll().stream().map(SchemaDetail::toResponse).toList();
    }

    @Override
    public List<ResponseSchemaDetail> findAllByHeaderId(int headerId) {
        return schemaDetailRepository.findAllBySchemaHeader_Id(headerId).stream().map(SchemaDetail::toResponse).toList();
    }

    @Override
    @Transactional
    public ResponseSchemaDetail updateSchemaDetail(int detailId, UpdateSchemaDetail request) {
        SchemaDetail schemaDetail = schemaDetailRepository.findById(detailId).orElseThrow(() -> new RuntimeException("Seat row bot found: " + detailId));

        schemaDetail.fromUpdate(request);
        return SchemaDetail.toResponse(schemaDetail);
    }
}
