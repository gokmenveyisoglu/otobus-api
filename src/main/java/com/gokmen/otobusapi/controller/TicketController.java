package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.record.Ticket.ResponseTicket;
import com.gokmen.otobusapi.service.TicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ticket")
@Tag(name = "Ticket")
public class TicketController {

    TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping()
    public List<ResponseTicket> getTickets() {
        return ticketService.getTickets();
    }

    @GetMapping("/{pnr}/tickets")
    public List<ResponseTicket> getTicketsByBookingReference(@PathVariable("pnr") String bookingReference) {
        return this.ticketService.getTicketsByBookingReference(bookingReference);
    }
}
