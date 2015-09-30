package org.winterchord.jsonapi.spec;

public class JsonApiDocument {
  public final JsonApiData data;
  public final JsonApiServer server;

  public JsonApiDocument(JsonApiServer server, JsonApiData data) {
    this.server = server;
    this.data = data;
  }
}
