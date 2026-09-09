package com.gokmen.otobusapi.repository.record.Ticket;

import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;

public record CreateTicket(CreateTraveller travellers, int seat) {
}
