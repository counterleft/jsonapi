package org.winterchord.jsonapi.fixtures;

import org.winterchord.jsonapi.JsonApiId;
import org.winterchord.jsonapi.JsonApiResource;

@JsonApiResource(name = "people")
public class Person {
  @JsonApiId
  private Long id;

  private String name;


  private Integer age;

  @SuppressWarnings("unused")
  public Person() {
    // for deserialization
  }

  public Person(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  @SuppressWarnings("unused")
  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  @SuppressWarnings("unused")
  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
}
