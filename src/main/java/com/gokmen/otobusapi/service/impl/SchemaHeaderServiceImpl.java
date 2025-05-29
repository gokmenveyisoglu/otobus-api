package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.SchemaHeaderRepository;
import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;
import com.gokmen.otobusapi.service.SchemaHeaderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchemaHeaderServiceImpl implements SchemaHeaderService {

    SchemaHeaderRepository schemaHeaderRepository;

    public SchemaHeaderServiceImpl(SchemaHeaderRepository schemaHeaderRepository){
        this.schemaHeaderRepository = schemaHeaderRepository;
    }

    @Override
    public void saveSchemaHeader(SchemaHeader schemaHeader) {

        schemaHeaderRepository.findAll().forEach(schemaHeader1 -> {
            String str1 = schemaHeader.getDescription().replaceAll("\\s+", "");
            String str2 = schemaHeader1.getDescription().replaceAll("\\s+", "");
            if (!str1.equals(str2))
                schemaHeaderRepository.save(schemaHeader);
            else
                System.out.println("Same schema header exist");
        });
    }

    @Override
    public void updateSchemaHeader(int headerId, SchemaHeader schemaHeader) {
        SchemaHeader updatedHeader = schemaHeaderRepository.findById(headerId).get();

        updatedHeader.setName(schemaHeader.getName());
        updatedHeader.setDescription(schemaHeader.getDescription());
        updatedHeader.setSchemaDetails(schemaHeader.getSchemaDetails());

        schemaHeaderRepository.save(updatedHeader);

    }

    @Override
    public List<SchemaHeader> findAll() {
        return schemaHeaderRepository.findAll();
    }

    @Override
    public Optional<SchemaHeader> getById(int id) {
        if(id == 0)
        return Optional.empty();
        else return schemaHeaderRepository.findById(id);
    }

    @Override
    public Optional<SchemaHeader> findByName(String name) {
        if (schemaHeaderRepository.findByName(name).isPresent())
            return schemaHeaderRepository.findByName(name);
        else
            return Optional.empty();
    }

    @Override
    public Optional<SchemaDetail> findDetails(String name) {
        if (schemaHeaderRepository.findByName(name).isPresent())
            return schemaHeaderRepository.findDetails(name);
        else return Optional.empty();    //Ex yap

    }


}
