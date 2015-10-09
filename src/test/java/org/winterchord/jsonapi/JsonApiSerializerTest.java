package org.winterchord.jsonapi;

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.winterchord.jsonapi.fixtures.MissingIdResource;
import org.winterchord.jsonapi.fixtures.MissingTypeResource;
import org.winterchord.jsonapi.fixtures.Person;
import org.winterchord.jsonapi.jackson.RequestBodyParseException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JsonApiSerializerTest {
  private JsonApiSerializer subject;
  private Person alice;
  private Person sally;
  private Person jane;

  @Before
  public void setUp() throws Exception {
    alice = new Person(123L, "Alice");
    sally = new Person(456L, "Sally");
    jane = new Person(789L, "Jane");
    jane.setAge(30);

    subject = new JsonApiSerializer();
  }

  @Test
  public void testSerializeSingle() throws Exception {
    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, "
        + "data: { type: 'people', id: '123', attributes: { name: 'Alice' } } }",
        subject.serialize(alice), JSONCompareMode.NON_EXTENSIBLE);

    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, "
            + "data: { type: 'people', id: '789', attributes: { name: 'Jane', age: 30 } } }",
        subject.serialize(jane), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerializeSingle_Null() throws Exception {
    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, data: null }",
        subject.serialize(null), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerializeMultiple() throws Exception {
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
  public void testSerializeMultiple_EmptyList() throws Exception {
    List<Person> resources = new ArrayList<>();

    JSONAssert.assertEquals("{ jsonapi: { version: '1.0' }, data: [] }",
        subject.serialize(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerializeMultiple_NullEntry() throws Exception {
    List<Person> resources = new ArrayList<>();
    resources.add(null);

    JSONAssert.assertEquals("{ jsonapi: { version: '1.0' }, data: [] }",
        subject.serialize(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testSerialize_MissingIdAnnotation() throws Exception {
    try {
      subject.serialize(new MissingIdResource("text"));
      fail("unexpected");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), containsString("Missing @JsonApiId"));
    }
  }

  @Test
  public void testDeserializeSingleSingleResource() throws Exception {
    Person actual = subject.deserializeData(
        "{ \"data\": { \"type\": \"people\", \"attributes\": {"
            + "\"name\": \"Sally\","
            + "\"age\": 12"
            + "} } }",
        Person.class
    );

    assertThat(actual.getName(), is("Sally"));
    assertThat(actual.getId(), nullValue());
    assertThat(actual.getAge(), is(12));
  }

  @Test
  public void testDeserializeSingle_WithId() throws Exception {
    Person actual = subject.deserializeData(
        "{ \"data\": { \"type\": \"people\", \"id\": \"987\", \"attributes\": {"
            + "\"name\": \"Sally\","
            + "\"age\": 12"
            + "} } }",
        Person.class
    );

    assertThat(actual.getName(), is("Sally"));
    assertThat(actual.getId(), is(987L));
    assertThat(actual.getAge(), is(12));
  }

  @Test
  public void testDeserializeSingle_Null() throws Exception {
    assertThat(subject.deserializeData(null, Person.class), nullValue());
  }

  @Test
  public void testDeserializeSingle_MissingTypeOnResource() throws Exception {
    try {
      subject.deserializeData(
          "{ \"data\": { \"type\": \"people\", \"id\": \"987\" } }",
          MissingTypeResource.class
      );

      fail("unexpected");
    } catch (Exception e) {
      assertThat(e.getMessage(), containsString("Missing @JsonApiResource"));
    }
  }

  @Test
  public void testDeserializeSingle_MissingTypeOnRequest() throws Exception {
    try {
      subject.deserializeData(
          "{ \"data\": { \"id\": \"987\", \"attributes\": {"
              + "\"name\": \"Sally\""
              + "} } }",
          Person.class
      );

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Missing type"));
    }
  }

  @Test
  public void testDeserializeSingle_NonTextualTypeOnRequest() throws Exception {
    try {
      subject.deserializeData(
          "{ \"data\": { \"type\": 12345, \"id\": \"987\", \"attributes\": {"
              + "\"name\": \"Sally\""
              + "} } }",
          Person.class
      );

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Invalid type"));
    }

    try {
      subject.deserializeData(
          "{ \"data\": { \"type\": null, \"id\": \"987\", \"attributes\": {"
              + "\"name\": \"Sally\""
              + "} } }",
          Person.class
      );

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Invalid type"));
    }
  }

  @Test
  public void testDeserializeSingle_NullData() throws Exception {
    assertThat(subject.deserializeData("{ \"data\": null }", Person.class), nullValue());
  }

  @Test
  public void testDeserializeSingle_EmptyData() throws Exception {
    try {
      subject.deserializeData(
          "{ \"data\": {} }",
          Person.class
      );

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Missing type"));
    }
  }

  @Test
  public void testDeserializeSingle_NullAttributes() throws Exception {
    Person actual = subject.deserializeData(
        "{ \"data\": { \"type\": \"people\", \"id\": \"987\", \"attributes\": null } } }",
        Person.class
    );

    assertThat(actual.getId(), is(987L));
    assertThat(actual.getName(), nullValue());
    assertThat(actual.getAge(), nullValue());
  }

  @Test
  public void testDeserializeSingle_EmptyAttributes() throws Exception {
    Person actual = subject.deserializeData(
        "{ \"data\": { \"type\": \"people\", \"id\": \"987\", \"attributes\": { } } }",
        Person.class
    );

    assertThat(actual.getId(), is(987L));
    assertThat(actual.getName(), nullValue());
    assertThat(actual.getAge(), nullValue());
  }
}
