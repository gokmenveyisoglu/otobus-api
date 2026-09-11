package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    boolean existsByBookingReference(String bookingReference);

    Optional<Booking> findByBookingReference(String bookingReference);
}
