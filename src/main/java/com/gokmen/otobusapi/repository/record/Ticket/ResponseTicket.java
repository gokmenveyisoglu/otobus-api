package com.gokmen.otobusapi.repository.record.Ticket;

import com.gokmen.otobusapi.repository.entities.Ticket.TicketStatus;
import com.gokmen.otobusapi.repository.record.Traveller.ResponseTraveller;

public record ResponseTicket(int ticketNumber, int bookingId, ResponseTraveller traveller, int fare, TicketStatus status, boolean active) {
}
