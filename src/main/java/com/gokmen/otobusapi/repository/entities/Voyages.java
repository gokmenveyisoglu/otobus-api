package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "voyages")
public class Voyages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int voyageId;

    private String voyageNo;

    private String voyageName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Route routes;

    @JsonIgnore
    @OneToMany(mappedBy = "voyageNo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Travellers> traveler;

    /*@JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "stationOrder")
    private List<Stations> firstStation; // atamaları yap

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "stationOrder")
    private List<Stations> lastStation;*/

    @JsonIgnore
    private int firstStation;

    @JsonIgnore
    private  int lastStation;

    private Date startDate;

    private Date endDate;

    private int voyagePrice;


}
