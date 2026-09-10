package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.record.Ticket.ResponseTicket;

import java.util.List;

public interface TicketService {

    List<ResponseTicket> getTickets();
}
