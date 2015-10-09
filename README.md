# jsonapi serializer + deserializer

[![Circle CI](https://circleci.com/gh/winterchord/jsonapi.svg?style=svg)](https://circleci.com/gh/winterchord/jsonapi)
[![Coverage Status](https://coveralls.io/repos/winterchord/jsonapi/badge.svg?branch=master&service=github)](https://coveralls.io/github/winterchord/jsonapi?branch=master)
[![Code Advisor On Demand
Status](https://badges.ondemand.coverity.com/streams/83ouvkifjl0jf787e1r0mpm1o4)](https://ondemand.coverity.com/streams/83ouvkifjl0jf787e1r0mpm1o4/jobs)

Library for serializing and deserializing java objects into
[jsonapi][jsonapi] resources.

The library uses annotations to label java classes that can be
serialized into jsonapi resources.

[jsonapi]: http://jsonapi.org

## Project Status

**Alpha**. This library is very experimental and its API is unstable.

Do _not_ use in production.

## User Guide

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](http://tools.ietf.org/html/rfc2119).

Read the [tests](https://github.com/winterchord/jsonapi/tree/master/src/test/java/org/winterchord/jsonapi) for more examples.

### Resource POJOs

A class annotated as a `@JsonApiResource` can be serialized to the jsonapi format.

```java
@JsonApiResource(name = "people")
public class Person {
  @JsonApiId
  private Long id;
  
  private String name;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
```

Every resource class MUST be annotated with `@JsonApiResource` and `@JsonApiId`.
Every resource class MUST adhere to the [JavaBean](https://en.wikipedia.org/wiki/JavaBeans#JavaBean_conventions) conventions.

This `Person` class will serialize into:

```javascript
{
  "jsonapi": {
    "version": "1.0"
  },
  "data": {
    "type": "people",
    "id": "123",
    "attributes": {
      "name": "Sally",
    }
  }
}
```

### Serialization

To serialize a single resource:

```java
String json = new JsonApiMarshal().dump(person);
```

To serialize a collection of resources:

```java
List<Person> people = ...;
String json = new JsonApiMarshal().dump(people);
```

### Deserialization

To deserialize a single resource:

```java
Person person = new JsonApiMarshal().load(json, Person.class);
```

## Development

Run tests with:

```
mvn verify
```

Run code coverage with:

```
mvn package jacoco:report
```

The coverage results are written to `target/site/jacoco/index.html`.
