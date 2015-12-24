package org.winterchord.jsonapi.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.winterchord.jsonapi.resource.ResourceInformation;
import org.winterchord.jsonapi.spec.JsonApiData;

import java.io.IOException;
import java.util.List;

public class JsonApiDataSerializer extends JsonSerializer<JsonApiData> {
  @Override
  public void serialize(JsonApiData value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    if (value.resourceInfoList.size() == 1) {
      serializeOne(jgen, value.resourceInfoList.get(0));
    }
    else {
      serializeMany(jgen, value.resourceInfoList);
    }
  }

  private void serializeOne(JsonGenerator jgen, ResourceInformation resourceInformation)
      throws IOException {
    jgen.writeStartObject();

    jgen.writeStringField("type", resourceInformation.type);
    jgen.writeStringField("id", resourceInformation.id);
    jgen.writeObjectField("attributes", resourceInformation.attributeMap);

    jgen.writeEndObject();
  }

  private void serializeMany(JsonGenerator jgen, List<ResourceInformation> resourceInfoList)
      throws IOException {
    jgen.writeStartArray();

    for (ResourceInformation resourceInformation : resourceInfoList) {
      serializeOne(jgen, resourceInformation);
    }

    jgen.writeEndArray();
  }
}
