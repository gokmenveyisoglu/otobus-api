package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gokmen.otobusapi.repository.entities.enums.SeatType;
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
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private SeatLayout seatLayout;

    private String seatNumber;
    private int rowNumber;
    private int columNumber;
    private int deckNumber;

    private SeatType type;

    private boolean active;
}

