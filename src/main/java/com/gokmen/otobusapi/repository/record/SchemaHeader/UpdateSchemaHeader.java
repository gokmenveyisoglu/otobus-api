package com.gokmen.otobusapi.repository.record.SchemaHeader;

import com.gokmen.otobusapi.repository.record.SchemaDetail.UpdateSchemaDetail;

import java.util.List;

public record UpdateSchemaHeader(String name, String description, List<UpdateSchemaDetail> schemaDetails) {
}
