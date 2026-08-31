package com.gokmen.otobusapi.repository.record.Bus;

public record CreateBusRequest(String number_plate, int schema_header_id, int route_id) {
}
