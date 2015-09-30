package org.winterchord.jsonapi.spec;

import org.winterchord.jsonapi.resource.ResourceInformation;

import java.util.List;

public class JsonApiData {
  public final List<ResourceInformation> resourceInfoList;

  public JsonApiData(List<ResourceInformation> resourceInfoList) {
    this.resourceInfoList = resourceInfoList;
  }
}
