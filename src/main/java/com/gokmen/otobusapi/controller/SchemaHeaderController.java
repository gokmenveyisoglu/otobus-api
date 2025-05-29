package com.gokmen.otobusapi.controller;


import com.gokmen.otobusapi.repository.record.SchemaHeader.CreateSchemaHeader;
import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.entities.SchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.UpdateSchemaHeader;
import com.gokmen.otobusapi.service.SchemaHeaderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("schema-header")
@Tag(name = "Şema ismi", description = "Şemanın genel ismini tasarlamak için")
public class SchemaHeaderController {

    private final SchemaHeaderService schemaHeaderService;

    public SchemaHeaderController(SchemaHeaderService schemaHeaderService){
        this.schemaHeaderService = schemaHeaderService;
    }

    @PostMapping()
    public void saveSchemaHeader(@RequestBody CreateSchemaHeader createSchemaHeader){

        SchemaHeader schemaHeader = SchemaHeader.fromCreate(createSchemaHeader);
        schemaHeaderService.saveSchemaHeader(schemaHeader);
    }
    @PutMapping("{id}")
    public void updateHeader(@PathVariable("id") int headerId, @RequestBody UpdateSchemaHeader updateSchemaHeader){
        SchemaHeader schemaHeader = SchemaHeader.fromUpdate(updateSchemaHeader);
        schemaHeaderService.updateSchemaHeader(headerId, schemaHeader);
    }
    @GetMapping()
    public List<SchemaHeader> findAll(){
        return schemaHeaderService.findAll();
    }
    @GetMapping("{hname}")
    public Optional<SchemaHeader> findByName(@PathVariable("hname") String name){

        return schemaHeaderService.findByName(name);
    }
    @GetMapping("{header}/detail")
    public Optional<SchemaDetail> findDetails(@PathVariable("header") String name){
        return schemaHeaderService.findDetails(name);
    }

}
