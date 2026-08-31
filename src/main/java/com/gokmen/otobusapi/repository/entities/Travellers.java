package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "travellers")
public class Travellers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int travellerId;

    private String travellerName;

    private String travellerSurname;

    private String gender;

    @JsonIgnore
    private boolean isForeign;

    private String identityNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyages voyages;

    @JsonIgnore
    private int seat;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boarding_station_id", nullable = false)
    private Stations boardingStation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drop_off_station_id", nullable = false)
    private Stations dropOffStation;
}
