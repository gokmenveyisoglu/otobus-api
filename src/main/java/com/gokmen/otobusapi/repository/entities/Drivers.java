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
@Table(name = "drivers")
public class Drivers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int driverId;

    private String driverName;

    private String driverSurname;

    private String licenseNumber;

    private String phoneNumber;

    private boolean active;

    @OneToMany
    private List<VoyageDriver> voyageDrivers;


//    @JsonIgnore
//    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
//    @JoinTable(name = "driver_buss_id", joinColumns = @JoinColumn(name = "driver_id"), inverseJoinColumns = @JoinColumn(name = "buss_id"))
//    private List<Buss> busId;

}
