package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.SchemaDetailRepository;
import com.gokmen.otobusapi.repository.SchemaHeaderRepository;
import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.service.SchemaDetailService;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class SchemaDetailServiceImpl implements SchemaDetailService {
    private final SchemaDetailRepository schemaDetailRepository;
    private final SchemaHeaderRepository schemaHeaderRepository;

    public SchemaDetailServiceImpl(SchemaDetailRepository schemaDetailRepository, SchemaHeaderRepository schemaHeaderRepository) {
        this.schemaDetailRepository = schemaDetailRepository;
        this.schemaHeaderRepository = schemaHeaderRepository;
    }

    @Override
    public void saveSchemaDetail(int headerId, List<SchemaDetail> schemaDetail) {

        schemaHeaderRepository.findById(headerId).ifPresent(schemaHeader -> schemaDetailRepository.saveAll(schemaDetail.stream().map(x -> { // map yerine peek e bak
            x.setSchemaHeader(schemaHeader);
            return x;
        }).toList()));

    }

    @Override
    public Optional<SchemaDetail> findById(int id) {
        return id <= 0 ? Optional.empty() : schemaDetailRepository.findById(id);
    }

    @Override
    public List<SchemaDetail> findAll() {
        return schemaDetailRepository.findAll();
    }

    @Override
    public void updateSchemaDetail(int detailId, SchemaDetail schemaDetail) {
        schemaDetailRepository.findById(detailId).ifPresent(schemaDetail1 -> {                                          //ifPresentOrElse yap
            schemaDetail1.setColumn1(schemaDetail.getColumn1());
            schemaDetail1.setColumn2(schemaDetail.getColumn2());
            schemaDetail1.setColumn4(schemaDetail.getColumn4());
            schemaDetail1.setColumn5(schemaDetail.getColumn5());
            schemaDetailRepository.save(schemaDetail1);
        });
    }
}
