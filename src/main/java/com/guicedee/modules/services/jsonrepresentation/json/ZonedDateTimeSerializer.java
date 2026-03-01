package com.guicedee.modules.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes zoned date-time values using ISO-8601 format.
 */
public class ZonedDateTimeSerializer
		extends JsonSerializer<LocalDateTime>
{
	public ZonedDateTimeSerializer()
	{
	}

	/**
	 * Writes the date-time as an ISO-8601 zoned date-time string.
	 *
	 * @param value the date-time to serialize
	 * @param generator the JSON generator
	 * @param provider the serializer provider
	 * @throws IOException when writing fails
	 */
	@Override
	public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
	}
}
