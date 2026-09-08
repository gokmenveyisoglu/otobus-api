package com.gokmen.otobusapi.repository.record.Booking;

import com.gokmen.otobusapi.repository.record.Ticket.ResponseTicket;
import com.gokmen.otobusapi.repository.record.User.ResponseUser;
import com.gokmen.otobusapi.repository.record.voyages.ResponseVoyage;

import java.time.Instant;
import java.util.List;

public record ResponseBooking(int bookingId, String bookingReference, ResponseUser user, ResponseVoyage voyage, Instant createdAt, List<ResponseTicket> ticket, boolean active) {
}
