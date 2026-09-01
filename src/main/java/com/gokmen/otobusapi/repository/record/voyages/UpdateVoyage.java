package com.gokmen.otobusapi.repository.record.voyages;

import java.util.Date;

public record UpdateVoyage(String voyageNo, String journeyNo, String voyageName, int voyagePrice, int routeId, int departureStationId, int arrivalStationId, int busId, Date startDate, Date endDate) {
}
