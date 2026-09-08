package com.gokmen.otobusapi.repository.record.Booking;

import com.gokmen.otobusapi.repository.record.Traveller.CreateTraveller;

import java.util.List;

public record CreateBooking(int userId, int voyageId, List<CreateTraveller> travellers) {
}
