package org.winterchord.jsonapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.winterchord.jsonapi.jackson.*;
import org.winterchord.jsonapi.resource.JsonApiDocumentRequest;
import org.winterchord.jsonapi.resource.ResourceInformation;
import org.winterchord.jsonapi.resource.ResourceScanner;
import org.winterchord.jsonapi.spec.JsonApiData;
import org.winterchord.jsonapi.spec.JsonApiDocument;
import org.winterchord.jsonapi.spec.JsonApiServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonApiMarshal {
  private final ObjectMapper mapper = new ObjectMapper();

  public JsonApiMarshal() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(JsonApiDocument.class, new JsonApiDocumentSerializer());
    module.addSerializer(JsonApiServer.class, new JsonApiServerSerializer());
    module.addSerializer(JsonApiData.class, new JsonApiDataSerializer());
    module.addDeserializer(JsonApiDocumentRequest.class, new JsonApiDocumentRequestDeserializer());
    mapper.registerModule(module);
  }

  public String dump(Object resource) {
    return dump(Collections.singletonList(resource));
  }

  public <R> String dump(Iterable<R> resources) {
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

  public <R> R load(String jsonApiDocumentString, Class<R> resourceClass) throws
      RequestBodyParseException {
    try {
      if (!isValidDeserializationInputs(jsonApiDocumentString, resourceClass)) {
        return null;
      }

      JsonApiDocumentRequest documentRequest =
          mapper.readValue(jsonApiDocumentString, JsonApiDocumentRequest.class);

      if (documentRequest == null) {
        return null;
      }

      R resourceInstance = resourceClass.newInstance();

      if (documentRequest.dataBody.id != null) {
        BeanUtils.setProperty(resourceInstance, "id", documentRequest.dataBody.id);
      }

      for (Map.Entry<String, Object> attributeEntry : documentRequest.dataBody.attributeMap
          .entrySet()) {
        JsonNode value = (JsonNode) attributeEntry.getValue();

        if (value.isValueNode()) {
          if (value.isTextual()) {
            PropertyUtils.setProperty(resourceInstance, attributeEntry.getKey(), value.asText());
          } else if (value.isInt()) {
            PropertyUtils.setProperty(resourceInstance, attributeEntry.getKey(), value.asInt());
          }
        }
      }

      return resourceInstance;
    } catch (RequestBodyParseException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private <R> boolean isValidDeserializationInputs(String jsonApiDocumentString,
      Class<R> resourceClass) {
    if (jsonApiDocumentString == null) {
      return false;
    }

    JsonApiResource jsonApiResourceAnnotation =
        resourceClass.getDeclaredAnnotation(JsonApiResource.class);

    if (jsonApiResourceAnnotation == null) {
      throw new IllegalStateException("Missing @JsonApiResource for resource: " + resourceClass);
    }

    return true;
  }
}
