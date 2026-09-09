package com.gokmen.otobusapi.repository.record.Traveller;

public record CreateTraveller(String travellerName, String travellerSurname, String gender, boolean isForeign, String identificationNumber) {
}