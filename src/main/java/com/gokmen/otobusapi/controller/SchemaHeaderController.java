package com.gokmen.otobusapi.controller;


import com.gokmen.otobusapi.repository.record.SchemaHeader.CreateSchemaHeader;
import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaHeader.ResponseSchemaHeader;
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

//    @PostMapping()
//    public void saveSchemaHeader(@RequestBody CreateSchemaHeader createSchemaHeader){
//
//        SchemaHeader schemaHeader = SchemaHeader.fromCreate(createSchemaHeader);
//        schemaHeaderService.saveSchemaHeader(schemaHeader);
//    }
    @PostMapping
    public ResponseSchemaHeader saveSchemaHeader(@RequestBody CreateSchemaHeader createSchemaHeader){
        return schemaHeaderService.saveSchemaHeader(createSchemaHeader);
    }
    @PutMapping("{id}")
    public ResponseSchemaHeader updateHeader(@PathVariable("id") int headerId, @RequestBody UpdateSchemaHeader updateSchemaHeader){
        return schemaHeaderService.updateSchemaHeader(headerId, updateSchemaHeader);
    }
    @PatchMapping("/{shi}/deactivate")
    public ResponseSchemaHeader deactivateById(@PathVariable("shi") int headerId) {
        return schemaHeaderService.deactivateById(headerId);
    }
    @GetMapping()
    public List<ResponseSchemaHeader> findAll(){
        return schemaHeaderService.findAll();
    }
    @GetMapping("id/{id}")
    public ResponseSchemaHeader findById(@PathVariable("id") int headerId) {
        return schemaHeaderService.getById(headerId);
    }
    @GetMapping("name/{hname}")
    public ResponseSchemaHeader findByName(@PathVariable("hname") String name){
        return schemaHeaderService.findByName(name);
    }
    @GetMapping("{header}/detail")
    public Optional<SchemaDetail> findDetails(@PathVariable("header") String name){
        return schemaHeaderService.findDetails(name);
    }

}
