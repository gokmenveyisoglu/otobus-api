package com.gokmen.otobusapi.repository.entities;

import com.gokmen.otobusapi.repository.entities.enums.DriverRole;
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
public class VoyageDriver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int voyageDriverId;

    @ManyToOne
    private Voyages voyages;
    @ManyToOne
    private Drivers drivers;

    private DriverRole role;
}
