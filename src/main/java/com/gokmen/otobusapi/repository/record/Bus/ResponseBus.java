package com.gokmen.otobusapi.repository.record.Bus;

import com.gokmen.otobusapi.repository.entities.Route;
import com.gokmen.otobusapi.repository.record.SchemaHeader.ResponseSchemaHeader;

public record ResponseBus(int bus_id, int max_traveller, String number_plate, ResponseSchemaHeader schemaHeader, Route route, boolean active) {
}
