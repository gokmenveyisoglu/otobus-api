package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT t FROM Ticket t WHERE t.status = :status AND t.booking.voyages.bus.bus_id = :busId AND t.booking.voyages.journeyNo = :journeyNo")
    List<Ticket> findActiveTicketsForJourney(@Param("busId") int busId, @Param("journeyNo") String journeyNo, @Param("status") Ticket.TicketStatus status);
}
