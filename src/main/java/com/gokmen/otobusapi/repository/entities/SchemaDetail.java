package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.*;
import com.gokmen.otobusapi.repository.record.SchemaDetail.CreateSchemaDetail;
import com.gokmen.otobusapi.repository.record.SchemaDetail.UpdateSchemaDetail;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
/*@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder*/
@Table(name = "schemaDetail")
//@JsonIgnoreProperties({"schemaDetails","schemaHeader"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class SchemaDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  int id;

    //@JsonIgnoreProperties("schemaDetails")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    //@EqualsAndHashCode.Exclude
    private SchemaHeader schemaHeader;

    private int column1;

    private int column2;

    //@Builder.Default
    private int column3 = 0;

    private int column4;

    private int column5;

    public static SchemaDetail fromCreate(CreateSchemaDetail createSchemaDetail){
        SchemaDetail schemaDetail = new SchemaDetail();
        schemaDetail.setColumn1(createSchemaDetail.column1());
        schemaDetail.setColumn2(createSchemaDetail.column2());
        schemaDetail.setColumn4(createSchemaDetail.column4());
        schemaDetail.setColumn5(createSchemaDetail.column5());
        schemaDetail.setSchemaHeader(createSchemaDetail.schemaHeader());
        return schemaDetail;
    }

    public static SchemaDetail fromUpdate(UpdateSchemaDetail updateSchemaDetail){
        SchemaDetail schemaDetail = new SchemaDetail();
        schemaDetail.setColumn1(updateSchemaDetail.column1());
        schemaDetail.setColumn2(updateSchemaDetail.column2());
        schemaDetail.setColumn4(updateSchemaDetail.column4());
        schemaDetail.setColumn5(updateSchemaDetail.column5());
        return schemaDetail;
    }



    public int getId() {
        return id;
    }


    public SchemaHeader getSchemaHeader() {
        return schemaHeader;
    }

    public void setSchemaHeader(SchemaHeader schemaHeader) {
        this.schemaHeader = schemaHeader;
    }

    public int getColumn1() {
        return column1;
    }

    public void setColumn1(int column1) {
        this.column1 = column1;
    }

    public int getColumn2() {
        return column2;
    }

    public void setColumn2(int column2) {
        this.column2 = column2;
    }

    public int getColumn3() {
        return column3;
    }

    public int getColumn4() {
        return column4;
    }

    public void setColumn4(int column4) {
        this.column4 = column4;
    }

    public int getColumn5() {
        return column5;
    }

    public void setColumn5(int column5) {
        this.column5 = column5;
    }
}