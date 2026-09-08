package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gokmen.otobusapi.repository.record.voyages.ResponseVoyage;
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

    private String journeyNo;

    private String voyageName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Route routes;

    @JsonIgnore
    @OneToMany(mappedBy = "voyageId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Travellers> traveler;

    /*@JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "stationOrder")
    private List<Stations> firstStation; // atamaları yap

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "stationOrder")
    private List<Stations> lastStation;*/

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false)
    private Buss bus;

    @JsonIgnore
    private int firstStation;

    @JsonIgnore
    private  int lastStation;

    private Date startDate;

    private Date endDate;

    private int voyagePrice;

    private boolean active = true;


    public static ResponseVoyage toResponse(Voyages voyages) {
        List<Stations> orderStations = voyages.getRoutes().getStations();

        int firstStationIndex = voyages.getFirstStation();
        int lastStationIndex = voyages.getLastStation();

        if (firstStationIndex < 0 || firstStationIndex >= orderStations.size())
            throw new RuntimeException("Voyage contain an invalid departure station position");
        if (lastStationIndex < 0 || lastStationIndex >= orderStations.size())
            throw new RuntimeException("Voyage contain an invalid arrival station position");

        int departureStationId = orderStations.get(firstStationIndex).getStationId();
        int arrivalStationId = orderStations.get(lastStationIndex).getStationId();

        return new ResponseVoyage(
                voyages.getVoyageId(),
                voyages.getVoyageNo(),
                voyages.getJourneyNo(),
                voyages.getVoyageName(),
                voyages.getRoutes(),
                departureStationId,
                arrivalStationId,
                Buss.toResponse(voyages.getBus()),
                voyages.getStartDate(),
                voyages.getEndDate(),
                voyages.getVoyagePrice(),
                voyages.isActive()
        );
    }
}
