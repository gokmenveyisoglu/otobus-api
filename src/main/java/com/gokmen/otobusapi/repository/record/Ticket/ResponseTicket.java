package com.gokmen.otobusapi.repository.record.Ticket;

import com.gokmen.otobusapi.repository.entities.Ticket.TicketStatus;
import com.gokmen.otobusapi.repository.record.Traveller.ResponseTraveller;

import java.util.Date;

public record ResponseTicket(int ticketNumber, int bookingId, ResponseTraveller traveller, int seat, int fare, int firstStation, int lastStation, Date travelStart, Date travelEnd, TicketStatus status) {
}
