package org.winterchord.jsonapi.resource;

public class JsonApiDocumentRequest {
  public ResourceInformation dataBody;

  public JsonApiDocumentRequest(ResourceInformation dataBody) {
    this.dataBody = dataBody;
  }
}
