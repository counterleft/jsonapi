package org.winterchord.jsonapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.winterchord.jsonapi.jackson.JsonApiDataSerializer;
import org.winterchord.jsonapi.jackson.JsonApiDocumentSerializer;
import org.winterchord.jsonapi.jackson.JsonApiServerSerializer;
import org.winterchord.jsonapi.resource.ResourceInformation;
import org.winterchord.jsonapi.resource.ResourceScanner;
import org.winterchord.jsonapi.spec.JsonApiData;
import org.winterchord.jsonapi.spec.JsonApiDocument;
import org.winterchord.jsonapi.spec.JsonApiServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonApiSerializer {
  private final ObjectMapper mapper = new ObjectMapper();

  public JsonApiSerializer() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(JsonApiDocument.class, new JsonApiDocumentSerializer());
    module.addSerializer(JsonApiServer.class, new JsonApiServerSerializer());
    module.addSerializer(JsonApiData.class, new JsonApiDataSerializer());
    mapper.registerModule(module);
  }

  public String serialize(Object resource) {
    return serialize(Collections.singletonList(resource));
  }

  public String serialize(Iterable resources) {
    JsonApiData data = null;

    if (resources != null) {
      ResourceScanner scanner = new ResourceScanner();

      List<ResourceInformation> resourceInfoList = new ArrayList<>();

      for (Object resource : resources) {
        if (resource != null) {
          resourceInfoList.add(scanner.scan(resource));
        }
      }

      data = new JsonApiData(resourceInfoList);
    }

    JsonApiServer server = new JsonApiServer();
    JsonApiDocument document = new JsonApiDocument(server, data);

    try {
      return mapper.writeValueAsString(document);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
