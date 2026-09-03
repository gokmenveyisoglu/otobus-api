package com.gokmen.otobusapi.repository.record.Traveller;

import com.gokmen.otobusapi.repository.record.Bus.ResponseBus;
import com.gokmen.otobusapi.repository.record.voyages.ResponseVoyage;

import java.util.Date;

public record ResponseTraveller(int traveller_id, String travellerName, String travellerSurname, String gender, boolean isForeign, String identificationNumber, ResponseBus busId, int seat, ResponseVoyage voyageId, int firstStation, int lastStation, Date travelStart, Date travelEnd, boolean active) {
}
