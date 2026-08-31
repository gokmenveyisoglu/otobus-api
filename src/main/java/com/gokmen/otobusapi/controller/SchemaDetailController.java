package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.record.SchemaDetail.CreateSchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaDetail.ResponseSchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaDetail.UpdateSchemaDetail;
import com.gokmen.otobusapi.service.SchemaDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("schema-detail")
@Tag(name = "Schema", description = "Şema tasarlamak için.")
public class SchemaDetailController {

    private final SchemaDetailService schemaDetailService;

    public SchemaDetailController(SchemaDetailService schemaDetailService){
        this.schemaDetailService = schemaDetailService;
    }
    @PostMapping("header/{hid}")
    public List<ResponseSchemaDetail> setSchema(@PathVariable("hid") int headerId, @RequestBody List<CreateSchemaDetail> request){

        return schemaDetailService.saveSchemaDetail(headerId, request);
    }
    @PutMapping("{id}")
    public ResponseSchemaDetail updateSchema(@PathVariable("id") int detailId, @RequestBody UpdateSchemaDetail schemaDetail){
        return schemaDetailService.updateSchemaDetail(detailId, schemaDetail);

    }
    @GetMapping("{id}")
    public ResponseSchemaDetail FindById(@PathVariable("id") int id){
        return schemaDetailService.findById(id);
    }
    @GetMapping()
    public List<ResponseSchemaDetail> findAll(){
        return schemaDetailService.findAll();
    }
    @GetMapping("header/{headerid}")
    public List<ResponseSchemaDetail> findByHeaderId(@PathVariable("headerid") int hid) {
        return schemaDetailService.findAllByHeaderId(hid);
    }


}
