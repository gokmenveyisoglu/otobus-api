package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gokmen.otobusapi.repository.record.Bus.ResponseBus;
import com.gokmen.otobusapi.repository.record.Voyage.ResponseVoyage;
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
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_station_id", nullable = false)
    private Stations departureStation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_station_id", nullable = false)
    private Stations arrivalStation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false)
    private Buss bus;

    @JsonIgnore
    @OneToMany(mappedBy = "voyages", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Travellers> traveler;

    private Date startDate;

    private Date endDate;

    private int voyagePrice;

    private boolean active = true;

    @Transient
    private void validateVoyageStation(Route route, Stations departureStation, Stations arrivalStation) {
        List<Stations> stations = route.getStations();

        int boardingIndex = findStationIndex(stations, departureStation.getStationId());

        int dropOffIndex = findStationIndex(stations, arrivalStation.getStationId());

        if (boardingIndex == -1) {
            throw new RuntimeException("Departure station is not part of the selected route");
        }

        if (dropOffIndex == -1) {
            throw new RuntimeException("Arrival station is not part of the selected route");
        }

        if (boardingIndex == dropOffIndex) {
            throw new RuntimeException("Departure and arrival stations cannot be the same");
        }
    }

    @Transient
    private int findStationIndex(List<Stations> stations, int stationId) {
        for (int index = 0; index < stations.size(); index++) {
            if (stations.get(index).getStationId() == stationId)
                return index;
        }
        return -1;
    }

    public static ResponseVoyage toResponse(Voyages voyages) {
        return new ResponseVoyage(
                voyages.getVoyageId(),
                voyages.getVoyageNo(),
                voyages.getVoyageName(),
                voyages.getRoute(),
                voyages.getDepartureStation(),
                voyages.getArrivalStation(),
                voyages.getStartDate(),
                voyages.getEndDate(),
                voyages.getVoyagePrice(),
                voyages.isActive()
        );
    }
}
