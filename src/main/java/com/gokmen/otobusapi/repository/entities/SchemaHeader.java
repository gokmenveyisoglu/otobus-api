package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.*;
import com.gokmen.otobusapi.repository.record.SchemaHeader.CreateSchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.ResponseSchemaHeader;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
/*@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder*/
@Table(name = "schemaHeader")
//@JsonIgnoreProperties({"schemaHeader","schemaDetails"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class SchemaHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private  String description;

    @OneToMany(mappedBy = "schemaHeader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @OrderColumn(name = "row_order")
    private List<SchemaDetail> schemaDetails = new ArrayList<>();

    @OneToMany(mappedBy = "schemaHeader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Buss> buss = new ArrayList<>();

    private boolean active = true;

    public void addSchemaDetail(SchemaDetail schemaDetail) {
        schemaDetails.add(schemaDetail);
        schemaDetail.setSchemaHeader(this);
    }

    public void replaceSchemaDetail(List<SchemaDetail> newSchemaDetails) {
        schemaDetails.clear();
        newSchemaDetails.forEach(this::addSchemaDetail);
    }

    public static SchemaHeader fromCreate(CreateSchemaHeader createSchemaHeader){
        SchemaHeader schemaHeader = new SchemaHeader();

        schemaHeader.setName(createSchemaHeader.name());
        schemaHeader.setDescription(createSchemaHeader.description());
        createSchemaHeader.schemaDetails().stream().map(SchemaDetail::fromCreate).forEach(schemaHeader::addSchemaDetail);

        return schemaHeader;
    }

    public static ResponseSchemaHeader toResponse(SchemaHeader schemaHeader){
        return new ResponseSchemaHeader(
                schemaHeader.getId(),
                schemaHeader.getName(),
                schemaHeader.getDescription(),
                schemaHeader.getSchemaDetails().stream().map(SchemaDetail::toResponse).toList(),
                schemaHeader.isActive()
        );
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SchemaDetail> getSchemaDetails() {
        return this.schemaDetails;
    }

    public void setSchemaDetails(List<SchemaDetail> schemaDetails) {
        this.schemaDetails = schemaDetails;
    }

    public List<Buss> getBuss() {
        return this.buss;
    }

    public void setBuss(List<Buss> buss) {
        this.buss = buss;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
