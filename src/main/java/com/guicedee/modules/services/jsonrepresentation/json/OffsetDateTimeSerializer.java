package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@link OffsetDateTime} values as ISO-8601 offset date-time strings.
 */
public class OffsetDateTimeSerializer
		extends ValueSerializer<OffsetDateTime>
{
	public OffsetDateTimeSerializer()
	{
	}

	/**
	 * Writes the offset date-time as an ISO-8601 string.
	 *
	 * @param value the offset date-time to serialize
	 * @param generator the JSON generator
	 * @param provider the serializer provider
	 * @throws IOException when writing fails
	 */
	@Override
	public void serialize(OffsetDateTime value, JsonGenerator generator, SerializationContext provider)
	{
		generator.writeString(convert(value));
	}
	
	/**
	 * Converts an offset date-time to a formatted ISO-8601 string.
	 *
	 * @param value the offset date-time to convert
	 * @return the formatted string, or null when input is null
	 */
	public String convert(OffsetDateTime value)
	{
		if (value == null)
		{
			return null;
		}
		return value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}
