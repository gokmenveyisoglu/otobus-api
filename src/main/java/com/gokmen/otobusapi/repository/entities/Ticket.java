package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gokmen.otobusapi.repository.record.Ticket.CreateTicket;
import com.gokmen.otobusapi.repository.record.Ticket.ResponseTicket;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    private Travellers traveller;

    private int seat;

    private int fare;

    @JsonIgnore
    private int firstStation;

    @JsonIgnore
    private int lastStation;

    @JsonIgnore
    private Date travelStart;

    @JsonIgnore
    private Date travelEnd;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public enum TicketStatus {
        CONFIRMED,
        CANCELLED
    }

    public static Ticket fromCreate(CreateTicket createTicket, Booking booking, Voyages voyages) {
        Ticket ticket = new Ticket();

        ticket.setBooking(booking);
        ticket.setTraveller(Travellers.fromCreate(createTicket.travellers()));
        ticket.setSeat(createTicket.seat());
        ticket.setFare(voyages.getVoyagePrice());
        ticket.setFirstStation(voyages.getFirstStation());
        ticket.setLastStation(voyages.getLastStation());
        ticket.setTravelStart(voyages.getStartDate());
        ticket.setTravelEnd(voyages.getEndDate());
        ticket.setStatus(TicketStatus.CONFIRMED);

        return ticket;
    }

    public static ResponseTicket toResponse(Ticket ticket) {
        return new ResponseTicket(
                ticket.getTicketNumber(),
                ticket.booking.getBookingId(),
                Travellers.toResponse(ticket.getTraveller()),
                ticket.getSeat(),
                ticket.getFare(),
                ticket.getFirstStation(),
                ticket.getLastStation(),
                ticket.getTravelStart(),
                ticket.getTravelEnd(),
                ticket.getStatus()
        );
    }
}

