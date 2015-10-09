package org.winterchord.jsonapi.resource;

import org.winterchord.jsonapi.JsonApiId;
import org.winterchord.jsonapi.JsonApiResource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ResourceScanner {
  public ResourceInformation scan(Object resource) {
    String id = null;
    Map<String, Object> attributeMapping = new HashMap<>();

    try {
      for (Field field : resource.getClass().getDeclaredFields()) {
        if (field.isSynthetic()) {
          continue;
        }

        field.setAccessible(true);

        if (field.isAnnotationPresent(JsonApiId.class)) {
          id = String.valueOf(field.get(resource));
        } else {
          if (field.get(resource) != null) {
            attributeMapping.put(field.getName(), field.get(resource));
          }
        }

        field.setAccessible(false);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    if (id == null) {
      throw new IllegalStateException("Missing @JsonApiId for resource: " + resource.getClass());
    }

    return new ResourceInformation(getResourceName(resource), id, attributeMapping);
  }

  private String getResourceName(Object resource) {
    JsonApiResource resourceAnnotation =
        resource.getClass().getDeclaredAnnotation(JsonApiResource.class);

    return resourceAnnotation.name();
  }

}
