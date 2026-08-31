package com.gokmen.otobusapi.repository.record.SchemaHeader;

import com.gokmen.otobusapi.repository.record.SchemaDetail.CreateSchemaDetail;

import java.util.List;

public record CreateSchemaHeader(String name, String description, List<CreateSchemaDetail> schemaDetails) {
}
