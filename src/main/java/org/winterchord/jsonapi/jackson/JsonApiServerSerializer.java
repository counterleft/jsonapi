package org.winterchord.jsonapi.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.winterchord.jsonapi.spec.JsonApiServer;

import java.io.IOException;

public class JsonApiServerSerializer extends JsonSerializer<JsonApiServer> {
  @Override
  public void serialize(JsonApiServer value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeStartObject();

    jgen.writeStringField("version", value.version);

    jgen.writeEndObject();
  }
}
