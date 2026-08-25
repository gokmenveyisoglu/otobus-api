package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gokmen.otobusapi.repository.entities.enums.VoyageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private String code;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Route routes;

    @ManyToOne
    private Buss buss;

    private LocalDateTime departureAt;
    private LocalDateTime arrivalAt;

    private BigDecimal basePrice;
    private VoyageStatus status;

    @OneToMany(mappedBy = "voyages")
    List<VoyageStops> voyageStops;
}
