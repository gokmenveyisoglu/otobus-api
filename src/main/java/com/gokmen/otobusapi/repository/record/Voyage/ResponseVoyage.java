package com.gokmen.otobusapi.repository.record.Voyage;

import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.entities.Stations;

import java.util.Date;

public record ResponseVoyage(int voyageId, String voyageNo, String voyageName, Route route, Stations departureStation, Stations arrivalStation, Date startDate, Date endDate, int voyagePrice, boolean active) {
}
