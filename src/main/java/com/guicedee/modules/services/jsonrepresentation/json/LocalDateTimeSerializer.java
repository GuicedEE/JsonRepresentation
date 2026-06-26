package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@link LocalDateTime} values using a configurable date-time pattern.
 */
public class LocalDateTimeSerializer
        extends ValueSerializer<LocalDateTime> {
    public static String LocalDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS";

    public LocalDateTimeSerializer() {
    }

    /**
     * Writes the date-time as a formatted string.
     *
     * @param value the date-time to serialize
     * @param generator the JSON generator
     * @param provider the serializer provider
     * @throws IOException when writing fails
     */
    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializationContext provider) {
        generator.writeString(convert(value));
    }
    
    /**
     * Converts a date-time to a string using {@link #LocalDateTimeFormat}.
     *
     * @param value the date-time to convert
     * @return the formatted string, or null when input is null
     */
    public String convert(LocalDateTime value)
    {
        if (value == null)
        {
            return null;
        }
        return value.format(DateTimeFormatter.ofPattern(LocalDateTimeFormat));
    }
}
