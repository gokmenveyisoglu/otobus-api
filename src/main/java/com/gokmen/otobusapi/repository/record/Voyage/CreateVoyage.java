package com.gokmen.otobusapi.repository.record.Voyage;

import java.util.Date;

public record CreateVoyage(String voyageNo, String voyageName, int routeId, int departureStationId, int arrivalStationId, Date startDate, Date endDate, int voyagePrice) {

}
