package org.winterchord.jsonapi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class JsonApiSerializerTest {
  private JsonApiSerializer subject;
  private Person alice;
  private Person sally;

  @Before
  public void setUp() throws Exception {
    alice = new Person(123L, "Alice");
    sally = new Person(456L, "Sally");

    subject = new JsonApiSerializer();
  }

  @Test
  public void testSerializeSingleResource() throws Exception {
    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, "
        + "data: { type: 'people', id: '123', attributes: { name: 'Alice' } } }",
        subject.serialize(alice), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerializeSingleResource_Null() throws Exception {
    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, data: null }",
        subject.serialize(null), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerializeMultipleResources() throws Exception {
    List<Person> resources = new ArrayList<>();
    resources.add(alice);
    resources.add(sally);

    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, data: ["
        + "{ type: 'people', id: '123', attributes: { name: 'Alice' } },"
        + "{ type: 'people', id: '456', attributes: { name: 'Sally' } }"
        + "] }",
        subject.serialize(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerializeMultipleResources_EmptyList() throws Exception {
    List<Person> resources = new ArrayList<>();

    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, data: [] }",
        subject.serialize(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerializeMultipleResources_NullEntry() throws Exception {
    List<Person> resources = new ArrayList<>();
    resources.add(null);

    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, data: [] }",
        subject.serialize(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerialize_MissingIdAnnotation() throws Exception {
    try {
      subject.serialize(new MissingIdResource("text"));
      Assert.fail("unexpected");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), containsString("Missing @JsonApiId"));
    }

  }

  @JsonApiResource(name = "people")
  private static class Person {
    @JsonApiId
    public final Long id;

    public final String name;

    public Person(Long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  @JsonApiResource(name = "missing-id-resources")
  private static class MissingIdResource {
    public final String text;

    private MissingIdResource(String text) {
      this.text = text;
    }
  }
}
