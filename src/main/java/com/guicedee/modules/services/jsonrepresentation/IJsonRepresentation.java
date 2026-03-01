package com.guicedee.modules.services.jsonrepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.guicedee.modules.services.jsonrepresentation.implementations.ObjectMapperBinder;
import com.guicedee.modules.services.jsonrepresentation.json.LaxJsonModule;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS;

/**
 * Convenience interface for JSON serialization/deserialization using a configured
 * Jackson {@link ObjectMapper}. Includes default instance helpers for round-tripping
 * and static helpers for reading from common sources.
 *
 * @param <J> the implementing type for fluent deserialization
 */
@SuppressWarnings("unused")
public interface IJsonRepresentation<J> extends Serializable
{
    /**
     * Applies the module's standard configuration to the supplied mapper.
     *
     * @param mapper the mapper to configure
     */
    static void configureObjectMapper(ObjectMapper mapper)
    {
        // Apply StreamReadConstraints to the mapper's existing factory so that
        // Vert.x DatabindCodec (and any other externally-created mapper) also
        // honours the increased max string length for large payloads.
        int maxStringLength = Integer.parseInt(
                System.getProperty("JSON_MAX_STRING_LENGTH",
                        System.getenv().getOrDefault("JSON_MAX_STRING_LENGTH", String.valueOf(250 * 1024 * 1024))));
        mapper.getFactory().setStreamReadConstraints(
                StreamReadConstraints.builder()
                        .maxStringLength(maxStringLength)
                        .build());

        mapper.registerModule(new LaxJsonModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
                .configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature(), true)
                .configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
                .enable(ALLOW_UNQUOTED_CONTROL_CHARS)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
    }

    /**
     * Serializes this object to JSON with pretty-printing enabled.
     *
     * @return the rendered JSON string
     */
    default String toJson()
    {
        return toJson(false);
    }

    /**
     * Serializes this object to JSON, optionally with compact output.
     *
     * @param tiny when true, disables indentation for compact output
     * @return the rendered JSON string
     */
    default String toJson(boolean tiny)
    {
        ObjectMapper objectMapper = ObjectMapperBinder.getObjectMapper();
        try
        {
            if (tiny)
            {
                return objectMapper.disable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(this);
            } else
            {
                return objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(this);
            }
        } catch (JsonProcessingException e)
        {
            throw new JsonRenderException("Unable to serialize as JSON", e);
        }
    }

    /**
     * Deserializes this object from a JSON string by updating the current instance.
     *
     * @param json the JSON payload
     * @return this instance with fields updated from JSON
     */
    default J fromJson(String json)
    {
        ObjectMapper objectMapper = ObjectMapperBinder.getObjectMapper();
        try
        {
            return objectMapper.readerForUpdating(this)
                    .readValue(json);
        } catch (IOException e)
        {
            throw new JsonRenderException("Unable to serialize as JSON", e);
        }
    }

    /**
     * Deserializes a JSON array into a list of elements.
     *
     * @param json the JSON array payload
     * @return a list of parsed elements
     */
    @SuppressWarnings({"UnusedReturnValue"})
    default List<J> fromJsonArray(String json)
    {
        ObjectMapper objectMapper = ObjectMapperBinder.getObjectMapper();
        try
        {
            return objectMapper.readerFor(new TypeReference<List<J>>()
                    {
                    })
                    .readValue(json);
        } catch (IOException e)
        {
            throw new JsonRenderException("Unable to serialize as JSON", e);
        }
    }

    /**
     * Deserializes a JSON array into a unique, sorted set of elements.
     *
     * @param json the JSON array payload
     * @param type the element type (unused but retained for signature clarity)
     * @return a sorted set of parsed elements
     */
    @SuppressWarnings({"UnusedReturnValue"})
    default Set<J> fromJsonArrayUnique(String json, @SuppressWarnings("unused")
    Class<J> type)
    {
        ObjectMapper objectMapper = ObjectMapperBinder.getObjectMapper();
        try
        {
            return objectMapper.readerFor(new TypeReference<TreeSet<J>>()
                    {
                    })
                    .readValue(json);
        } catch (IOException e)
        {
            throw new JsonRenderException("Unable to serialize as JSON", e);
        }
    }

    /**
     * Reads a JSON value from an input stream.
     *
     * @param <T>   the target type
     * @param file  the stream to read from
     * @param clazz the target class
     * @return the parsed instance
     * @throws IOException when the stream cannot be read or parsed
     */
    static <T> T From(InputStream file, Class<T> clazz) throws IOException
    {
        return getJsonObjectReader().forType(clazz)
                .readValue(file);
    }


    /**
     * Reads a JSON value from a file.
     *
     * @param <T>   the target type
     * @param file  the file to read from
     * @param clazz the target class
     * @return the parsed instance
     * @throws IOException when the file cannot be read or parsed
     */
    static <T> T From(File file, Class<T> clazz) throws IOException
    {
        return getJsonObjectReader().forType(clazz)
                .readValue(file);
    }

    /**
     * Reads a JSON value from a reader.
     *
     * @param <T>   the target type
     * @param file  the reader to read from
     * @param clazz the target class
     * @return the parsed instance
     * @throws IOException when the reader cannot be read or parsed
     */
    static <T> T From(Reader file, Class<T> clazz) throws IOException
    {
        return getJsonObjectReader().forType(clazz)
                .readValue(file);
    }

    /**
     * Returns the configured {@link ObjectMapper}.
     *
     * @return the shared object mapper
     */
    static ObjectMapper getObjectMapper()
    {
        return ObjectMapperBinder.getObjectMapper();
    }

    /**
     * Returns an {@link ObjectReader} configured for lenient parsing defaults.
     *
     * @return a configured reader
     */
    static ObjectReader getJsonObjectReader()
    {
        return ObjectMapperBinder.getObjectMapper().reader()
                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Reads a JSON value from a string.
     *
     * @param <T>     the target type
     * @param content the JSON payload
     * @param clazz   the target class
     * @return the parsed instance
     * @throws IOException when the content cannot be parsed
     */
    static <T> T From(String content, Class<T> clazz) throws IOException
    {
        return getJsonObjectReader().forType(clazz)
                .readValue(content);
    }

    /**
     * Reads a JSON value from a URL.
     *
     * @param <T>     the target type
     * @param content the URL to read from
     * @param clazz   the target class
     * @return the parsed instance
     * @throws IOException when the URL cannot be read or parsed
     */
    static <T> T From(URL content, Class<T> clazz) throws IOException
    {
        return getJsonObjectReader().forType(clazz)
                .readValue(content);
    }


    /**
     * Reads a JSON array from an input stream into a list.
     *
     * @param <T>   the element type
     * @param file  the stream to read from
     * @param clazz the element class
     * @return a list of parsed elements
     * @throws JsonRenderException when the stream cannot be read or parsed
     */
    static <T> List<T> fromToList(InputStream file, Class<T> clazz)
    {
        T list = null;
        try
        {
            list = ObjectMapperBinder.getObjectMapper()
                    .reader()
                    .forType(clazz)
                    .readValue(file);
        } catch (IOException e)
        {
            throw new JsonRenderException("Unable to read the input stream ", e);
        }
        ArrayList<T> lists = new ArrayList<>();
        lists.addAll(Arrays.asList((T[]) list));
        return lists;
    }

    /**
     * Reads a JSON array from a URL into a list.
     *
     * @param <T>     the element type
     * @param content the URL to read from
     * @param clazz   the element class
     * @return a list of parsed elements
     * @throws IOException when the URL cannot be read or parsed
     */
    static <T> List<T> fromToList(URL content, Class<T> clazz) throws IOException
    {
        T list = ObjectMapperBinder.getObjectMapper()
                .reader()
                .forType(clazz)
                .readValue(content);
        ArrayList<T> lists = new ArrayList<>();
        lists.addAll(Arrays.asList((T[]) list));
        return lists;
    }

    /**
     * Reads a JSON array from a file into a list.
     *
     * @param <T>   the element type
     * @param file  the file to read from
     * @param clazz the element class
     * @return a list of parsed elements
     * @throws IOException when the file cannot be read or parsed
     */
    static <T> List<T> fromToList(File file, Class<T> clazz) throws IOException
    {
        T list = ObjectMapperBinder.getObjectMapper()
                .reader()
                .forType(clazz)
                .readValue(file);
        ArrayList<T> lists = new ArrayList<>();
        lists.addAll(Arrays.asList((T[]) list));
        return lists;
    }

    /**
     * Reads a JSON array from a reader into a list.
     *
     * @param <T>   the element type
     * @param file  the reader to read from
     * @param clazz the element class
     * @return a list of parsed elements
     * @throws IOException when the reader cannot be read or parsed
     */
    static <T> List<T> fromToList(Reader file, Class<T> clazz) throws IOException
    {
        T list = ObjectMapperBinder.getObjectMapper()
                .reader()
                .forType(clazz)
                .readValue(file);
        ArrayList<T> lists = new ArrayList<>();
        lists.addAll(Arrays.asList((T[]) list));
        return lists;
    }

    /**
     * Reads a JSON array from a string into a list.
     *
     * @param <T>     the element type
     * @param content the JSON array payload
     * @param clazz   the element class
     * @return a list of parsed elements
     * @throws IOException when the content cannot be parsed
     */
    static <T> List<T> fromToList(String content, Class<T> clazz) throws IOException
    {
        T list = ObjectMapperBinder.getObjectMapper()
                .reader()
                .forType(clazz)
                .readValue(content);
        ArrayList<T> lists = new ArrayList<>();
        lists.addAll(Arrays.asList((T[]) list));
        return lists;
    }

}
