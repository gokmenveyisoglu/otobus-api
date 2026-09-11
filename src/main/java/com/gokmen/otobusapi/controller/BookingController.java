package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.record.Booking.CreateBooking;
import com.gokmen.otobusapi.repository.record.Booking.ResponseBooking;
import com.gokmen.otobusapi.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("booking")
@Tag(name = "Booking")
public class BookingController {

    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping()
    public List<ResponseBooking> getBookings() {
        return bookingService.getBookings();
    }
    @GetMapping("/{pnr}/ticket")
    public ResponseBooking findBookingByReferenceNumber(@PathVariable("pnr") String reference) {
        return this.bookingService.getBookingByReferenceNumber(reference);
    }
    @GetMapping("/occupied-seats/{voyageId}")
    public List<Integer> getOccupiedSeats(@PathVariable("voyageId") int voyageId) {
        return bookingService.getOccupiedSeats(voyageId);
    }

    @PostMapping()
    public ResponseBooking seyBooking(@RequestBody CreateBooking request) {
        return bookingService.setBooking(request);
    }
    @PatchMapping("{id}/deactivate")
    public ResponseBooking deactivateBooking(@PathVariable("id") int bookingId) {
        return bookingService.deactivateBooking(bookingId);
    }
}
