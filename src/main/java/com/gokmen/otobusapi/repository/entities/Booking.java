package com.gokmen.otobusapi.repository.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gokmen.otobusapi.repository.record.Booking.ResponseBooking;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookingId;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Voyages voyages;

    private Instant createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Builder.Default
    private boolean active = true;

//    public static Booking fromCreate(CreateBooking createBooking) {
//        Booking booking = new Booking();
//
//        createBooking.travellers().stream().map()
//    }

    public static ResponseBooking toResponse(Booking booking) {
        return new ResponseBooking(
                booking.getBookingId(),
                booking.getBookingReference(),
                User.toResponse(booking.getUser()),
                Voyages.toResponse(booking.getVoyages()),
                booking.getCreatedAt(),
                booking.getTickets().stream().map(Ticket::toResponse).toList(),
                booking.isActive()
        );
    }
}
