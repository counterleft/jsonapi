package org.winterchord.jsonapi.resource;

import java.util.Map;

public class ResourceInformation {
  public final String type;
  public final String id;
  public final Map<String, Object> attributeMap;

  public ResourceInformation(String type, String id, Map<String, Object> attributeMap) {
    this.type = type;
    this.id = id;
    this.attributeMap = attributeMap;
  }
}
