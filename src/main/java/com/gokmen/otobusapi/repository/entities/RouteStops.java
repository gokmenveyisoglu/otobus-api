package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class RouteStops {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int routeStopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Route route;

    @ManyToOne
    private Stations stations;

    private int stopOrder;

    private int arrivalOffsetMinutes;
    private int departureOffsetMinutes;

    private boolean boardingAllowed;
    private boolean dropOffAllowed;
}
