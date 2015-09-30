package org.winterchord.jsonapi.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.winterchord.jsonapi.spec.JsonApiDocument;

import java.io.IOException;

public class JsonApiDocumentSerializer extends JsonSerializer<JsonApiDocument> {
  @Override
  public void serialize(JsonApiDocument value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeStartObject();

    jgen.writeObjectField("jsonapi", value.server);
    jgen.writeObjectField("data", value.data);

    jgen.writeEndObject();
  }
}
