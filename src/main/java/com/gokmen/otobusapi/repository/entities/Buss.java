package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
/*@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder*/
@Table(name = "buss")
public class Buss {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bus_id;

    @ManyToOne
    private Carrier carrier;

    @ManyToOne
    private SeatLayout seatLayout;

    private String plateNumber;
    private String brand;
    private String model;

    Integer modelYear;

    boolean wifiAvailable;
    boolean powerOutletAvailable;
    boolean airConditionerAvailable;
    boolean active;
}
