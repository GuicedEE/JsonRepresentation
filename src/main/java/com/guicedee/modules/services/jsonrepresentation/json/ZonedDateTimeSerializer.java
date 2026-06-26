package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes zoned date-time values using ISO-8601 format.
 */
public class ZonedDateTimeSerializer
		extends ValueSerializer<LocalDateTime>
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
	public void serialize(LocalDateTime value, JsonGenerator generator, SerializationContext provider)
	{
		generator.writeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
	}
}
