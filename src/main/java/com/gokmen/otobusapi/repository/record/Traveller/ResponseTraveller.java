package com.gokmen.otobusapi.repository.record.Traveller;

public record ResponseTraveller(int traveller_id, String travellerName, String travellerSurname, String gender, boolean isForeign, String identificationNumber, boolean active) {
}
