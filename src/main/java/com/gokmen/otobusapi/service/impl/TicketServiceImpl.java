package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.TicketRepository;
import com.gokmen.otobusapi.repository.entities.Ticket;
import com.gokmen.otobusapi.repository.record.Ticket.ResponseTicket;
import com.gokmen.otobusapi.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<ResponseTicket> getTickets() {
        return ticketRepository.findAll().stream().map(Ticket::toResponse).toList();
    }

    @Override
    public List<ResponseTicket> getTicketsByBookingReference(String bookingReference) {
        return ticketRepository.findTicketsByBookingReference(bookingReference).stream().map(Ticket::toResponse).toList();
    }
}
