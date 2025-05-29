package com.gokmen.otobusapi.repository.record.SchemaHeader;

import com.gokmen.otobusapi.repository.entities.Buss;
import com.gokmen.otobusapi.repository.entities.SchemaDetail;

import java.util.List;

public record ResponseSchemaHeader(int id, String name, String description, List<SchemaDetail> schemaDetails, List<Buss> busses) {
}
