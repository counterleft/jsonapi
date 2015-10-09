package org.winterchord.jsonapi.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.winterchord.jsonapi.resource.JsonApiDocumentRequest;
import org.winterchord.jsonapi.resource.ResourceInformation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonApiDocumentRequestDeserializer extends JsonDeserializer<JsonApiDocumentRequest> {
  @Override
  public JsonApiDocumentRequest deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext) throws IOException {
    ObjectNode rootNode = jsonParser.readValueAsTree();

    if (rootNode == null) {
      return null;
    }

    JsonNode dataNode = rootNode.get("data");

    if (dataNode == null || dataNode.isNull()) {
      return null;
    }

    if (dataNode.get("type") == null) {
      throw new RequestBodyParseException("Missing type field in data object");
    }
    else if (!dataNode.get("type").isTextual()) {
      throw new RequestBodyParseException("Invalid type field in data object");
    }

    String type = dataNode.get("type").asText();

    String id = (dataNode.get("id") != null) ? dataNode.get("id").asText() : null;

    JsonNode attributesNode = dataNode.get("attributes");

    Map<String, Object> attributeMap = new HashMap<>();

    Iterator<Map.Entry<String, JsonNode>> fieldIterator = attributesNode.fields();
    while (fieldIterator.hasNext()) {
      Map.Entry<String, JsonNode> attributeEntry = fieldIterator.next();

      JsonNode attributeValue =
          (attributeEntry.getValue() != null) ? attributeEntry.getValue() : null;

      attributeMap.put(attributeEntry.getKey(), attributeValue);
    }

    ResourceInformation dataBody = new ResourceInformation(type, id, attributeMap);

    return new JsonApiDocumentRequest(dataBody);
  }
}
