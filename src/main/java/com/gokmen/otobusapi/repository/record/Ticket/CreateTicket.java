package com.gokmen.otobusapi.repository.record.Ticket;

import com.gokmen.otobusapi.repository.entities.Booking;
import com.gokmen.otobusapi.repository.entities.Ticket.TicketStatus;
import com.gokmen.otobusapi.repository.entities.Travellers;

public record CreateTicket(Booking booking, Travellers travellers, int fare, TicketStatus status) {
}
