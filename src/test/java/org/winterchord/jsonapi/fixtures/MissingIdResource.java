package org.winterchord.jsonapi.fixtures;

import org.winterchord.jsonapi.JsonApiResource;

@JsonApiResource(name = "missing-id-resources")
public class MissingIdResource {
  public final String text;

  public MissingIdResource(String text) {
    this.text = text;
  }
}
