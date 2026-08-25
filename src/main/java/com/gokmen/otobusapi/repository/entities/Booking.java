package com.gokmen.otobusapi.repository.entities;

import com.gokmen.otobusapi.repository.entities.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookingId;

    private String referenceCode;

    @ManyToOne
    private Voyages voyages;

    private String contactFirstName;
    private String contactLastName;
    private String contactEmail;
    private String contactPhone;

    private BookingStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;
    private LocalDateTime expireAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;

    @OneToMany(mappedBy = "booking")
    private List<Ticket> tickets;
}
