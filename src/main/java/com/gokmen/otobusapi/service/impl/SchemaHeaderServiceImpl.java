package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.SchemaHeaderRepository;
import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.CreateSchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.ResponseSchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.UpdateSchemaHeader;
import com.gokmen.otobusapi.service.SchemaHeaderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SchemaHeaderServiceImpl implements SchemaHeaderService {

    SchemaHeaderRepository schemaHeaderRepository;

    public SchemaHeaderServiceImpl(SchemaHeaderRepository schemaHeaderRepository){
        this.schemaHeaderRepository = schemaHeaderRepository;
    }

//    @Override
//    public void saveSchemaHeader(SchemaHeader schemaHeader) {
//
//        if (!schemaHeaderRepository.findAll().isEmpty()){
//            schemaHeaderRepository.findAll().forEach(schemaHeader1 -> {
//                String str1 = schemaHeader.getDescription().replaceAll("\\s+", "");
//                String str2 = schemaHeader1.getDescription().replaceAll("\\s+", "");
//                if (!str1.equals(str2))
//                    schemaHeaderRepository.save(schemaHeader);
//                else
//                    System.out.println("Same schema header exist");
//            });
//        } else
//            schemaHeaderRepository.save(schemaHeader);
//
//    }

    @Override
    @Transactional
    public ResponseSchemaHeader saveSchemaHeader(CreateSchemaHeader request) {
        SchemaHeader schemaHeader = SchemaHeader.fromCreate(request);
        SchemaHeader savedHeader = schemaHeaderRepository.save(schemaHeader);
        return SchemaHeader.toResponse(savedHeader);
    }

    @Override
    @Transactional
    public ResponseSchemaHeader updateSchemaHeader(int headerId, UpdateSchemaHeader request) {
        SchemaHeader updatedHeader = schemaHeaderRepository.findById(headerId).orElseThrow(() -> new RuntimeException("Seat layout not found: " + headerId));

        updatedHeader.setName(request.name());
        updatedHeader.setDescription(request.description());

        List<SchemaDetail> newDetail = request.schemaDetails().stream().map(detailRequest -> {
            SchemaDetail detail = new SchemaDetail();
            detail.fromUpdate(detailRequest);
            return detail;
        }).toList();

        updatedHeader.replaceSchemaDetail(newDetail);

        SchemaHeader savedHeader = schemaHeaderRepository.saveAndFlush(updatedHeader);

        return SchemaHeader.toResponse(savedHeader);
    }

    @Override
    @Transactional
    public ResponseSchemaHeader deactivateById(int headerId) {
        SchemaHeader deactivatedHeader = schemaHeaderRepository.findById(headerId).orElseThrow(() -> new RuntimeException("Seat layout not found: " + headerId));
        deactivatedHeader.setActive(false);
        SchemaHeader savedHeader = schemaHeaderRepository.saveAndFlush(deactivatedHeader);
        return SchemaHeader.toResponse(savedHeader);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSchemaHeader> findAll() {
        return schemaHeaderRepository.findAll().stream().map(SchemaHeader::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseSchemaHeader getById(int id) {
        SchemaHeader schemaHeader = schemaHeaderRepository.findById(id).orElseThrow(() -> new RuntimeException("Seat layout not found: " + id));
        return SchemaHeader.toResponse(schemaHeader);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseSchemaHeader findByName(String name) {
        SchemaHeader schemaHeader = schemaHeaderRepository.findByName(name).orElseThrow(() -> new RuntimeException("Seat layout not found: " + name));
        return SchemaHeader.toResponse(schemaHeader);
    }

    @Override
    public Optional<SchemaDetail> findDetails(String name) {
        if (schemaHeaderRepository.findByName(name).isPresent())
            return schemaHeaderRepository.findDetails(name);
        else return Optional.empty();    //Ex yap

    }


}
