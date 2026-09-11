package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.record.Booking.CreateBooking;
import com.gokmen.otobusapi.repository.record.Booking.ResponseBooking;

import java.util.List;

public interface BookingService {
    List<ResponseBooking> getBookings();
    ResponseBooking getBookingByReferenceNumber(String reference);
    ResponseBooking setBooking(CreateBooking request);
    ResponseBooking deactivateBooking(int bookingId);
    List<Integer> getOccupiedSeats(int voyageId);
}
