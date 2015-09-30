package org.winterchord.jsonapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonApiResource {
  /**
   * The "type" of the resource.
   *
   * @return The type of the "type" of the resource.
   */
  String name();
}
