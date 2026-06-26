package com.guicedee.modules.services.jsonrepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import com.guicedee.modules.services.jsonrepresentation.implementations.ObjectMapperBinder;
import com.guicedee.modules.services.jsonrepresentation.json.LaxJsonModule;

import java.io.*;
import java.net.URL;
import java.util.*;

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
     * <p>
     * Jackson 3 mappers are immutable, so this returns a reconfigured copy of the
     * supplied mapper rather than mutating it in place.
     *
     * @param mapper the mapper to base the configuration on
     * @return a new, configured {@link ObjectMapper}
     */
    static ObjectMapper configureObjectMapper(ObjectMapper mapper)
    {
        return mapper.rebuild()
                .addModule(new LaxJsonModule())
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .changeDefaultVisibility(vc -> vc
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE))
                .build();
    }

    /**
     * Registers an additional Jackson module onto the shared mappers.
     * <p>
     * Jackson 3 mappers are immutable, so this rebuilds the shared instances with the supplied
     * module applied. Intended for plugins that contribute custom (de)serializers at startup.
     *
     * @param module the Jackson module to add
     */
    static void registerModule(tools.jackson.databind.JacksonModule module)
    {
        ObjectMapperBinder.registerModule(module);
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
                return objectMapper.writer()
                        .without(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(this);
            } else
            {
                return objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(this);
            }
        } catch (JacksonException e)
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
        } catch (JacksonException e)
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
        } catch (JacksonException e)
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
        } catch (JacksonException e)
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
        try (InputStream stream = content.openStream())
        {
            return getJsonObjectReader().forType(clazz)
                    .readValue(stream);
        }
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
        } catch (JacksonException e)
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
        T list;
        try (InputStream stream = content.openStream())
        {
            list = ObjectMapperBinder.getObjectMapper()
                    .reader()
                    .forType(clazz)
                    .readValue(stream);
        }
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
