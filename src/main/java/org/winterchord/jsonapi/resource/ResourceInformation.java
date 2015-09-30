package org.winterchord.jsonapi.resource;

import java.util.Map;

public class ResourceInformation {
  public final String type;
  public final String id;
  public final Map<String, String> attributeMap;

  public ResourceInformation(String type, String id, Map<String, String> attributeMap) {
    this.type = type;
    this.id = id;
    this.attributeMap = attributeMap;
  }
}
