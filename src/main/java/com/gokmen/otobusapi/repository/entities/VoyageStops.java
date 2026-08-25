package com.gokmen.otobusapi.repository.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class VoyageStops {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int voyageStopsId;

    @ManyToOne
    private Voyages voyages;
    @ManyToOne
    private RouteStops routeStops;

    private LocalDateTime boardingAllowed;
    private LocalDateTime departureAllowed;
}
