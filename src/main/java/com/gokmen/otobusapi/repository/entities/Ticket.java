package com.gokmen.otobusapi.repository.entities;

import com.gokmen.otobusapi.repository.entities.enums.Gender;
import com.gokmen.otobusapi.repository.entities.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ticketId;

    private String ticketNumber;

    @ManyToOne
    private Booking booking;
    @ManyToOne
    private Seat seat;

    @ManyToOne
    private VoyageStops boardingStop;
    @ManyToOne
    private VoyageStops destinationStop;

    private String passengerFirstName;
    private String passengerLastName;
    private String identityNumber;

    private boolean foreignPassenger;
    private Gender gender;

    private BigDecimal price;
    private TicketStatus status;
}
