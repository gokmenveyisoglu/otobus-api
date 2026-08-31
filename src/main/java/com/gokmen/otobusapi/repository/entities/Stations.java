package com.gokmen.otobusapi.repository.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "stations")
public class Stations {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int stationId;

    private String station_name;

    private String address;

    /*@JsonIgnore
    @ManyToMany
    private List<Voyages> stationOrder;*/

    private int stationOrder;


    @ManyToMany(mappedBy = "stations", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Route> route;


}

