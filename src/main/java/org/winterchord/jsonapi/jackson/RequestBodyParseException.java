package org.winterchord.jsonapi.jackson;

public class RequestBodyParseException extends RuntimeException {
  public RequestBodyParseException(String message) {
    super(message);
  }
}
