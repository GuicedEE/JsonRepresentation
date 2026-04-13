# GuicedEE JSON Representation

[![Maven Central](https://img.shields.io/maven-central/v/com.guicedee.modules.representations/json-representation)](https://central.sonatype.com/artifact/com.guicedee.modules.representations/json-representation)
[![Maven Snapshot](https://img.shields.io/nexus/s/com.guicedee.modules.representations/json-representation?server=https%3A%2F%2Foss.sonatype.org&label=Maven%20Snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/guicedee/modules/representations/json-representation/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](https://www.apache.org/licenses/LICENSE-2.0)

![Java 25+](https://img.shields.io/badge/Java-25%2B-green)
![Guice 7](https://img.shields.io/badge/Guice-7%2B-green)
![Jackson](https://img.shields.io/badge/Jackson-2.21.x-green)
![Maven 4](https://img.shields.io/badge/Maven-4%2B-green)

Pre-configured [Jackson](https://github.com/FasterXML/jackson) ObjectMapper setup, Guice bindings, and a mixin interface for painless JSON serialization/deserialization across the [GuicedEE](https://github.com/GuicedEE) ecosystem.

Provides two ready-to-use `ObjectMapper` instances (standard JSON and JavaScript-friendly), a `LaxJsonModule` with lenient type handling for dates, booleans, and integers, and the `IJsonRepresentation<J>` interface that gives any class one-liner `toJson()` / `fromJson()` methods.

Built on [Jackson Databind](https://github.com/FasterXML/jackson-databind) · JPMS module `com.guicedee.jsonrepresentation` · Java 25+

## 📦 Installation

```xml
<dependency>
  <groupId>com.guicedee.modules.representations</groupId>
  <artifactId>json-representation</artifactId>
</dependency>
```

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
implementation("com.guicedee.modules.representations:json-representation:2.0.0-RC5")
```
</details>

## 🚀 Quick Start

### Mixin interface — zero boilerplate

Implement `IJsonRepresentation` on any class and you get `toJson()` and `fromJson()` for free:

```java
public class Order implements IJsonRepresentation<Order> {
    private String id;
    private LocalDate placed;
    private boolean paid;
}

// Serialize
Order order = new Order();
String json = order.toJson();       // pretty-printed
String tiny = order.toJson(true);   // compact

// Deserialize — updates in place
order.fromJson(json);

// Deserialize a list
List<Order> orders = order.fromJsonArray(jsonArray);
```

### Static helpers — read from any source

```java
// From a String
Order o = IJsonRepresentation.From(jsonString, Order.class);

// From a File, InputStream, Reader, or URL
Order o = IJsonRepresentation.From(file, Order.class);
Order o = IJsonRepresentation.From(inputStream, Order.class);
Order o = IJsonRepresentation.From(url, Order.class);

// Arrays → List
List<Order> list = IJsonRepresentation.fromToList(inputStream, Order.class);
List<Order> list = IJsonRepresentation.fromToList(jsonString, Order.class);
```

### Direct ObjectMapper access

```java
ObjectMapper mapper = IJsonRepresentation.getObjectMapper();
ObjectReader reader = IJsonRepresentation.getJsonObjectReader();
```

## ⚙️ ObjectMapper Configuration

`IJsonRepresentation.configureObjectMapper(mapper)` applies the module's standard settings to **any** ObjectMapper — useful for configuring Vert.x `DatabindCodec` or third-party mappers:

| Setting | Value |
|---|---|
| `WRITE_DATES_AS_TIMESTAMPS` | `false` — ISO-8601 strings |
| `FAIL_ON_UNKNOWN_PROPERTIES` | `false` — lenient deserialization |
| `FAIL_ON_MISSING_CREATOR_PROPERTIES` | `false` |
| `READ_UNKNOWN_ENUM_VALUES_AS_NULL` | `true` |
| `ESCAPE_NON_ASCII` | `true` |
| `ALLOW_UNQUOTED_FIELD_NAMES` | `true` (reading) |
| `ALLOW_SINGLE_QUOTES` | `true` (reading) |
| `ALLOW_UNQUOTED_CONTROL_CHARS` | `true` |
| Field visibility | `ANY` — fields serialized directly |
| Getter/setter visibility | `NONE` — no getter/setter interference |

### Max string length

Large payloads are supported out of the box. The maximum JSON string length defaults to **250 MB** and can be tuned via:

- System property: `-DJSON_MAX_STRING_LENGTH=52428800`
- Environment variable: `JSON_MAX_STRING_LENGTH=52428800`

## 🔗 Guice Bindings — `ObjectMapperBinder`

`ObjectMapperBinder` is an `IGuiceModule` discovered automatically via ServiceLoader / JPMS.
It binds the following keys into the Guice injector (all from `ObjectBinderKeys`):

| Guice Key | Type | Description |
|---|---|---|
| `DefaultObjectMapper` | `ObjectMapper` | Standard mapper (singleton by default) |
| `JSONObjectWriter` | `ObjectWriter` | Pretty-printed, quoted field names |
| `JSONObjectWriterTiny` | `ObjectWriter` | Compact output, quoted field names |
| `JSONObjectReader` | `ObjectReader` | Lenient reader (accepts arrays, ignores unknowns) |
| `JavascriptObjectMapper` | `ObjectMapper` | JavaScript-friendly (unquoted keys, non-null) |
| `JavaScriptObjectWriter` | `ObjectWriter` | Pretty-printed, unquoted field names |
| `JavaScriptObjectWriterTiny` | `ObjectWriter` | Compact, unquoted field names |
| `JavaScriptObjectReader` | `ObjectReader` | Lenient reader for JS-style JSON |

Inject them anywhere:

```java
@Inject @Named("JSON")
private ObjectWriter jsonWriter;

@Inject
private ObjectMapper mapper;  // DefaultObjectMapper
```

Toggle singleton mode if needed:

```java
ObjectMapperBinder.singleton = false; // default is true
```

## 🕐 LaxJsonModule — Lenient Type Handling

The `LaxJsonModule` is a Jackson `SimpleModule` registered on both mappers. It provides relaxed serializers and deserializers for common Java types that often arrive in inconsistent formats from external APIs.

### Date/Time types

| Java Type | Serializer | Deserializer | Key Deserializer |
|---|---|---|---|
| `LocalDate` | `LocalDateSerializer` | `LocalDateDeserializer` | `LocalDateDeserializerKey` |
| `LocalTime` | `LocalTimeSerializer` | `LocalTimeDeserializer` | — |
| `LocalDateTime` | `LocalDateTimeSerializer` | `LocalDateTimeDeserializer` | `LocalDateTimeDeserializerKey` |
| `Instant` | `InstantSerializer` | `InstantDeserializer` | — |
| `OffsetDateTime` | `OffsetDateTimeSerializer` | `OffsetDateTimeDeserializer` | `OffsetDateTimeDeserializerKey` |
| `OffsetTime` | `OffsetTimeSerializer` | `OffsetTimeDeserializer` | — |
| `ZonedDateTime` | — | `ZonedDateTimeDeserializer` | — |
| `Duration` | `DurationToString` | `StringToDurationTimeSeconds` | — |

All deserializers accept **multiple formats** — ISO-8601, epoch millis, numeric strings, and common variants — so you never have to worry about date format mismatches from upstream systems.

### Scalar types

| Java Type | Deserializer | Behaviour |
|---|---|---|
| `Boolean` / `boolean` | `StringToBoolean` / `StringToBool` | Accepts `"1"`, `"yes"`, `"on"`, `"true"`, `"y"` → `true`; `"0"`, `"no"`, `"off"`, `"false"`, `"n"` → `false` |
| `Integer` / `int` | `StringToIntegerRelaxed` / `StringToIntRelaxed` | Parses numeric strings leniently, handles blank/null gracefully |
| `Charset` | `StringToCharacterSet` | Parses charset names from strings |

## 📋 Static Constants — `StaticStrings`

`StaticStrings` centralises commonly used string and character constants (content-type headers, path separators, quote characters, etc.) to avoid magic literals scattered across the codebase.

```java
StaticStrings.HTML_HEADER_JSON           // "application/json"
StaticStrings.HTML_HEADER_JAVASCRIPT     // "application/javascript"
StaticStrings.STRING_EMPTY               // ""
StaticStrings.STRING_FORWARD_SLASH       // "/"
```

## ⚠️ Exception Handling

`JsonRenderException` is an unchecked `RuntimeException` thrown by all `IJsonRepresentation` default methods when serialization or deserialization fails. It wraps the underlying Jackson exception so callers get a clean stack trace without checked-exception noise.

## 🗺️ Module Graph

```
com.guicedee.jsonrepresentation
 ├── com.fasterxml.jackson.databind       (ObjectMapper, serializers)
 ├── com.fasterxml.jackson.core           (streaming API)
 ├── com.fasterxml.jackson.datatype.jsr310 (Java Time support)
 ├── com.guicedee.client                  (SPI contracts, ObjectBinderKeys)
 └── org.apache.commons.lang3
```

## 🧩 JPMS

Module name: **`com.guicedee.jsonrepresentation`**

```java
module com.guicedee.jsonrepresentation {
    exports com.guicedee.modules.services.jsonrepresentation;
    exports com.guicedee.modules.services.jsonrepresentation.json;

    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.core;
    requires com.guicedee.client;

    provides IGuiceModule
        with ObjectMapperBinder;
}
```

## 🤝 Contributing

Issues and pull requests are welcome.

## 📄 License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
