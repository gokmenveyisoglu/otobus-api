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
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String routeNo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "station_route", joinColumns = @JoinColumn(name = "route_id"), inverseJoinColumns = @JoinColumn(name = "stations_station_id"))
    @OrderColumn(name = "station_order")
    private List<Stations> stations;

    @JsonIgnore
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Buss> buss;

    @JsonIgnore
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Voyages> voyages;

    private Date startDate;

    private Date endDate;

    private boolean active;
}
