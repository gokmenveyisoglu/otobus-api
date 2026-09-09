package com.gokmen.otobusapi.repository.record.Booking;

import com.gokmen.otobusapi.repository.record.Ticket.CreateTicket;

import java.util.List;

public record CreateBooking(int userId, int voyageId, List<CreateTicket> tickets) {
}
