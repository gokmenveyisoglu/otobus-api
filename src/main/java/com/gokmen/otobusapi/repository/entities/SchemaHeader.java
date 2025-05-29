package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.*;
import com.gokmen.otobusapi.repository.record.SchemaHeader.CreateSchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.ResponseSchemaHeader;
import com.gokmen.otobusapi.repository.record.SchemaHeader.UpdateSchemaHeader;
import jakarta.persistence.*;

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


    @OneToMany(mappedBy = "schemaHeader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SchemaDetail> schemaDetails;

    @OneToMany(mappedBy = "schemaHeader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Buss> buss;


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




    public static SchemaHeader fromCreate(CreateSchemaHeader createSchemaHeader){
        SchemaHeader schemaHeader = new SchemaHeader();
        schemaHeader.setName(createSchemaHeader.name());
        schemaHeader.setDescription(createSchemaHeader.description());
        return schemaHeader;
    }
    public static SchemaHeader fromUpdate(UpdateSchemaHeader updateSchemaHeader){
        SchemaHeader schemaHeader = new SchemaHeader();
        schemaHeader.setName(updateSchemaHeader.name());
        schemaHeader.setDescription(updateSchemaHeader.description());
        schemaHeader.setSchemaDetails(updateSchemaHeader.schemaDetails());
        return schemaHeader;

    }
    public static ResponseSchemaHeader toResponse(SchemaHeader schemaHeader){
        return new ResponseSchemaHeader(schemaHeader.getId(), schemaHeader.getName(), schemaHeader.getDescription(), schemaHeader.getSchemaDetails(), schemaHeader.getBuss());
    }
}
