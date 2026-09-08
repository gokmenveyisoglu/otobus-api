package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gokmen.otobusapi.repository.record.Ticket.CreateTicket;
import com.gokmen.otobusapi.repository.record.Ticket.ResponseTicket;
import jakarta.persistence.*;
import lombok.*;

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

    private int fare;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Builder.Default
    private boolean active = true;

    public enum TicketStatus {
        CONFIRMED,
        CANCELLED
    }

    public static Ticket fromCreate(CreateTicket createTicket) {
        Ticket ticket = new Ticket();

        ticket.setBooking(createTicket.booking());
        ticket.setTraveller(createTicket.travellers());
        ticket.setFare(createTicket.fare());
        ticket.setStatus(createTicket.status());

        return ticket;
    }

    public static ResponseTicket toResponse(Ticket ticket) {
        return new ResponseTicket(
                ticket.getTicketNumber(),
                ticket.booking.getBookingId(),
                Travellers.toResponse(ticket.getTraveller()),
                ticket.getFare(),
                ticket.getStatus(),
                ticket.isActive()
        );
    }
}

