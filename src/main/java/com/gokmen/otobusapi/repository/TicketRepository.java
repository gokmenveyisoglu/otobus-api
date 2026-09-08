package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}
