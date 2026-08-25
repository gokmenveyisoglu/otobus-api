package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table
public class SeatLayout {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int seatLayoutId;

    private String name;
    private String description;

    private int deckCount;
    boolean active;

    @OneToMany(mappedBy = "seatLayout")
    @JsonManagedReference
    private List<Seat> seats;

    @OneToMany(mappedBy = "seatLayout")
    private List<Buss> busses;

}
