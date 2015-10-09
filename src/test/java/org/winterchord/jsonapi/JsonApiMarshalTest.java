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

public class JsonApiMarshalTest {
  private JsonApiMarshal subject;
  private Person alice;
  private Person sally;
  private Person jane;

  @Before
  public void setUp() throws Exception {
    alice = new Person(123L, "Alice");
    sally = new Person(456L, "Sally");
    jane = new Person(789L, "Jane");
    jane.setAge(30);

    subject = new JsonApiMarshal();
  }

  @Test
  public void testDumpSingle() throws Exception {
    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, "
        + "data: { type: 'people', id: '123', attributes: { name: 'Alice' } } }",
        subject.dump(alice), JSONCompareMode.NON_EXTENSIBLE);

    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, "
            + "data: { type: 'people', id: '789', attributes: { name: 'Jane', age: 30 } } }",
        subject.dump(jane), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testDumpSingle_Null() throws Exception {
    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, data: null }",
        subject.dump(null), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testDumpMultiple() throws Exception {
    List<Person> resources = new ArrayList<>();
    resources.add(alice);
    resources.add(sally);

    JSONAssert.assertEquals(
        "{ jsonapi: { version: '1.0' }, data: ["
        + "{ type: 'people', id: '123', attributes: { name: 'Alice' } },"
        + "{ type: 'people', id: '456', attributes: { name: 'Sally' } }"
        + "] }",
        subject.dump(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testDumpMultiple_EmptyList() throws Exception {
    List<Person> resources = new ArrayList<>();

    JSONAssert.assertEquals("{ jsonapi: { version: '1.0' }, data: [] }",
        subject.dump(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testDumpMultiple_NullEntry() throws Exception {
    List<Person> resources = new ArrayList<>();
    resources.add(null);

    JSONAssert.assertEquals("{ jsonapi: { version: '1.0' }, data: [] }",
        subject.dump(resources), JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void testDump_MissingIdAnnotation() throws Exception {
    try {
      subject.dump(new MissingIdResource("text"));
      fail("unexpected");
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), containsString("Missing @JsonApiId"));
    }
  }

  @Test
  public void testLoadSingle() throws Exception {
    Person actual = subject.load(
        "{ \"data\": { \"type\": \"people\", \"attributes\": {" + "\"name\": \"Sally\","
            + "\"age\": 12" + "} } }", Person.class);

    assertThat(actual.getName(), is("Sally"));
    assertThat(actual.getId(), nullValue());
    assertThat(actual.getAge(), is(12));
  }

  @Test
  public void testLoadSingle_WithId() throws Exception {
    Person actual = subject.load(
        "{ \"data\": { \"type\": \"people\", \"id\": \"987\", \"attributes\": {"
            + "\"name\": \"Sally\"," + "\"age\": 12" + "} } }", Person.class);

    assertThat(actual.getName(), is("Sally"));
    assertThat(actual.getId(), is(987L));
    assertThat(actual.getAge(), is(12));
  }

  @Test
  public void testLoadSingle_Null() throws Exception {
    assertThat(subject.load(null, Person.class), nullValue());
  }

  @Test
  public void testLoadSingle_MissingTypeOnResource() throws Exception {
    try {
      subject.load("{ \"data\": { \"type\": \"people\", \"id\": \"987\" } }",
          MissingTypeResource.class);

      fail("unexpected");
    } catch (Exception e) {
      assertThat(e.getMessage(), containsString("Missing @JsonApiResource"));
    }
  }

  @Test
  public void testLoadSingle_MissingTypeOnRequest() throws Exception {
    try {
      subject.load(
          "{ \"data\": { \"id\": \"987\", \"attributes\": {" + "\"name\": \"Sally\"" + "} } }",
          Person.class);

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Missing type"));
    }
  }

  @Test
  public void testLoadSingle_NonTextualTypeOnRequest() throws Exception {
    try {
      subject.load("{ \"data\": { \"type\": 12345, \"id\": \"987\", \"attributes\": {"
          + "\"name\": \"Sally\"" + "} } }", Person.class);

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Invalid type"));
    }

    try {
      subject.load(
          "{ \"data\": { \"type\": null, \"id\": \"987\", \"attributes\": {" + "\"name\": \"Sally\""
              + "} } }", Person.class);

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Invalid type"));
    }
  }

  @Test
  public void testLoadSingle_NullData() throws Exception {
    assertThat(subject.load("{ \"data\": null }", Person.class), nullValue());
  }

  @Test
  public void testLoadSingle_EmptyData() throws Exception {
    try {
      subject.load("{ \"data\": {} }", Person.class);

      fail("unexpected");
    } catch (RequestBodyParseException e) {
      assertThat(e.getMessage(), containsString("Missing type"));
    }
  }

  @Test
  public void testLoadSingle_NullAttributes() throws Exception {
    Person actual = subject.load(
        "{ \"data\": { \"type\": \"people\", \"id\": \"987\", \"attributes\": null } } }",
        Person.class);

    assertThat(actual.getId(), is(987L));
    assertThat(actual.getName(), nullValue());
    assertThat(actual.getAge(), nullValue());
  }

  @Test
  public void testLoadSingle_EmptyAttributes() throws Exception {
    Person actual = subject.load(
        "{ \"data\": { \"type\": \"people\", \"id\": \"987\", \"attributes\": { } } }",
        Person.class);

    assertThat(actual.getId(), is(987L));
    assertThat(actual.getName(), nullValue());
    assertThat(actual.getAge(), nullValue());
  }
}
