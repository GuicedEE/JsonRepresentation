package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@link LocalTime} values using a fixed HHmmss pattern.
 */
public class LocalTimeSerializer
		extends ValueSerializer<LocalTime>
{
	public static final DateTimeFormatter[] formats = new DateTimeFormatter[]{
			DateTimeFormatter.ofPattern("HHmmss"),
			DateTimeFormatter.ofPattern("HH:mm:ss"),
			DateTimeFormatter.ofPattern("HH:mm:ss.SSS"),
			DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"),
			DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS"),
			DateTimeFormatter.ofPattern("HHmm"),
	};
	
	public LocalTimeSerializer()
	{
	}
	
	/**
	 * Writes the time as a formatted string.
	 *
	 * @param value the time to serialize
	 * @param generator the JSON generator
	 * @param provider the serializer provider
	 * @throws IOException when writing fails
	 */
	@Override
	public void serialize(LocalTime value, JsonGenerator generator, SerializationContext provider)
	{
		generator.writeString(convert(value));
	}
	
	/**
	 * Converts a time to a HHmmss string.
	 *
	 * @param value the time to convert
	 * @return the formatted string, or null when input is null
	 */
	public String convert(LocalTime value)
	{
		if (value == null)
		{
			return null;
		}
		return value.format(DateTimeFormatter.ofPattern("HHmmss"));
	}
}
