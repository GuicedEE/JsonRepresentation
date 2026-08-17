package com.guicedee.modules.services.jsonrepresentation.implementations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.guicedee.client.services.lifecycle.IGuiceModule;
import com.guicedee.client.implementations.ObjectBinderKeys;
import com.guicedee.modules.services.jsonrepresentation.json.LaxJsonModule;

import lombok.Getter;
import lombok.extern.java.Log;

import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

import static com.guicedee.client.implementations.ObjectBinderKeys.DefaultObjectMapper;
import static com.guicedee.client.implementations.ObjectBinderKeys.JavaScriptObjectWriter;

/**
 * Guice module that binds shared {@link ObjectMapper} instances and configured
 * reader/writer providers used by the JSON representation layer.
 * <p>
 * Jackson 3 mappers are immutable; all configuration is therefore applied via the
 * {@link JsonMapper.Builder} at construction time.
 */
@Log
public class ObjectMapperBinder
        extends AbstractModule
        implements IGuiceModule<ObjectMapperBinder>
{

    /**
     * Maximum allowed string length for JSON parsing (default 250 MB).
     * Configurable via the system property / environment variable {@code JSON_MAX_STRING_LENGTH}.
     */
    private static final int MAX_STRING_LENGTH = Integer.parseInt(
            System.getProperty("JSON_MAX_STRING_LENGTH",
                    System.getenv().getOrDefault("JSON_MAX_STRING_LENGTH", String.valueOf(250 * 1024 * 1024))));

    /**
     * If the object mapper must behave as a singleton
     */
    public static boolean singleton = true;

    @Getter
    private static ObjectMapper objectMapper = buildMapper(true, true);

    @Getter
    private static ObjectMapper javaScriptObjectMapper = buildMapper(false, false);

    /**
     * Registers an additional Jackson {@link tools.jackson.databind.JacksonModule} onto the shared
     * mappers. Jackson 3 mappers are immutable, so this rebuilds the shared instances with the
     * extra module applied. Intended to be called during startup (e.g. by plugins that contribute
     * custom (de)serializers); callers obtaining a mapper afterwards via
     * {@link #getObjectMapper()} receive the reconfigured instance.
     *
     * @param module the Jackson module to add
     */
    public static synchronized void registerModule(tools.jackson.databind.JacksonModule module)
    {
        if (module == null)
        {
            return;
        }
        objectMapper = objectMapper.rebuild().addModule(module).build();
        javaScriptObjectMapper = javaScriptObjectMapper.rebuild().addModule(module).build();
    }

    /**
     * Builds a fully configured immutable {@link ObjectMapper} for Jackson 3.
     *
     * @param quotePropertyNames    whether property names should be quoted on write
     * @param caseInsensitiveEnums  whether enum parsing should be case-insensitive
     * @return a configured object mapper instance
     */
    private static ObjectMapper buildMapper(boolean quotePropertyNames, boolean caseInsensitiveEnums)
    {
        JsonFactory factory = JsonFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(MAX_STRING_LENGTH)
                        .build())
                .build();

        JsonMapper.Builder builder = JsonMapper.builder(factory)
                .addModule(new LaxJsonModule())
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .configure(JsonWriteFeature.ESCAPE_NON_ASCII, true)
                .configure(JsonWriteFeature.QUOTE_PROPERTY_NAMES, quotePropertyNames)
                .configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true)
                .configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
                .changeDefaultVisibility(vc -> vc
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE))
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL));

        if (caseInsensitiveEnums)
        {
            builder = builder.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        }

        return builder.build();
    }

    /**
     * Binds configured {@link ObjectMapper} instances and related readers/writers
     * into the Guice registry.
     */
    @Override
    public void configure()
    {
        log.config("Bound ObjectMapper (DefaultObjectMapper) as singleton [" + singleton + "]");
        var p = (Provider<ObjectMapper>) () -> objectMapper;
        if (singleton)
        {
            bind(DefaultObjectMapper)
                    .toProvider(p)
                    .in(Singleton.class);
        }
        else
        {
            bind(DefaultObjectMapper)
                    .toProvider(p);
        }

        log.finest("Bound ObjectWriter.class @Named(JSON)");

        bind(ObjectBinderKeys.JSONObjectWriter)
                .toProvider(() ->
                        objectMapper
                                .writerWithDefaultPrettyPrinter()
                                .with(SerializationFeature.INDENT_OUTPUT)
                                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                                .with(JsonWriteFeature.QUOTE_PROPERTY_NAMES)
                                .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                                .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));

        bind(ObjectBinderKeys.JSONObjectWriterTiny)
                .toProvider(() ->
                        objectMapper
                                .writer()
                                .without(SerializationFeature.INDENT_OUTPUT)
                                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                                .with(JsonWriteFeature.QUOTE_PROPERTY_NAMES)
                                .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                                .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));

        bind(ObjectBinderKeys.JSONObjectReader)
                .toProvider(() ->
                        objectMapper
                                .reader()
                                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                                .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                );

        log.finest("Bound ObjectWriter.class @Named(JavaScriptObjectReader)");
        bind(ObjectBinderKeys.JavascriptObjectMapper)
                .toInstance(javaScriptObjectMapper);


        bind(JavaScriptObjectWriter)
                .toProvider(() ->
                        javaScriptObjectMapper
                                .writerWithDefaultPrettyPrinter()
                                .with(SerializationFeature.INDENT_OUTPUT)
                                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                                .without(JsonWriteFeature.QUOTE_PROPERTY_NAMES)
                                .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                                .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));

        bind(ObjectBinderKeys.JavaScriptObjectWriterTiny)
                .toProvider(() ->
                        javaScriptObjectMapper
                                .writer()
                                .without(SerializationFeature.INDENT_OUTPUT)
                                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                                .without(JsonWriteFeature.QUOTE_PROPERTY_NAMES)
                                .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                                .withoutFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));

        bind(ObjectBinderKeys.JavaScriptObjectReader)
                .toProvider(() ->
                        javaScriptObjectMapper
                                .reader()
                                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                                .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                );
    }
}
