package com.gokmen.otobusapi.repository.record.SchemaHeader;

import com.gokmen.otobusapi.repository.entities.SchemaDetail;

import java.util.List;

public record UpdateSchemaHeader(String name, String description, List<SchemaDetail> schemaDetails) {
}
