package com.gokmen.otobusapi.repository.record.voyages;

import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.record.Bus.ResponseBus;

import java.util.Date;

public record ResponseVoyage(int voyageId, String voyageNo, String journeyNo, String voyageName, Route route, int departureStationId, int arrivalStationId, ResponseBus bus, Date startDate, Date endDate, int voyagePrice, boolean active) {
}
