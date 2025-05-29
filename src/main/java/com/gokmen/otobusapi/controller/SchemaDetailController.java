package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;
import com.gokmen.otobusapi.service.SchemaDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("schema-detail")
@Tag(name = "Schema", description = "Şema tasarlamak için.")
public class SchemaDetailController {

    private final SchemaDetailService schemaDetailService;

    public SchemaDetailController(SchemaDetailService schemaDetailService){
        this.schemaDetailService = schemaDetailService;
    }

    @PostMapping("header/{hid}")
    public void setSchema(@PathVariable("hid") int headerId,@RequestBody List<SchemaDetail> schemaDetail){

        schemaDetailService.saveSchemaDetail(headerId, schemaDetail);
    }

    @PutMapping("{id}")
    public void updateSchema(@PathVariable("id") int detailId, @RequestBody SchemaDetail schemaDetail){
        schemaDetailService.updateSchemaDetail(detailId, schemaDetail);

    }

    @GetMapping("{id}")
    public Optional<SchemaDetail> FindById(@PathVariable("id") int id){
        return schemaDetailService.findById(id);
    }
    @GetMapping()
    public List<SchemaDetail> findAll(){
        return schemaDetailService.findAll();
    }


}
