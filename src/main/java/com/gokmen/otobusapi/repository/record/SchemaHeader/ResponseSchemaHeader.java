package com.gokmen.otobusapi.repository.record.SchemaHeader;

import com.gokmen.otobusapi.repository.record.SchemaDetail.ResponseSchemaDetail;

import java.util.List;

public record ResponseSchemaHeader(int id, String name, String description, List<ResponseSchemaDetail> schemaDetails, boolean active) {
}
