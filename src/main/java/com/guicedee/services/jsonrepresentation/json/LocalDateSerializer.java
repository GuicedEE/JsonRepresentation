package com.guicedee.services.jsonrepresentation.json;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@link LocalDate} values using a configurable date pattern.
 */
public class LocalDateSerializer
        extends JsonSerializer<LocalDate> {
    public static String LocalDateFormat = "yyyy-MM-dd";

    public LocalDateSerializer() {
    }

    /**
     * Writes the date as a formatted string.
     *
     * @param value the date to serialize
     * @param generator the JSON generator
     * @param provider the serializer provider
     * @throws IOException when writing fails
     */
    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(convert(value));
    }
    
    /**
     * Converts a date to a string using {@link #LocalDateFormat}.
     *
     * @param value the date to convert
     * @return the formatted string, or null when input is null
     */
    public String convert(LocalDate value)
    {
        if (value == null)
        {
            return null;
        }
        return value.format(DateTimeFormatter.ofPattern(LocalDateFormat));
    }
}
