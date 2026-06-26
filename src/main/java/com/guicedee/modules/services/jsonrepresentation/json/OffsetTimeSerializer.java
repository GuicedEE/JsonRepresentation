package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@link OffsetTime} values as ISO-8601 offset time strings.
 */
public class OffsetTimeSerializer
		extends ValueSerializer<OffsetTime>
{
	public OffsetTimeSerializer()
	{
	}

	/**
	 * Writes the offset time as an ISO-8601 string.
	 *
	 * @param value the offset time to serialize
	 * @param generator the JSON generator
	 * @param provider the serializer provider
	 * @throws IOException when writing fails
	 */
	@Override
	public void serialize(OffsetTime value, JsonGenerator generator, SerializationContext provider)
	{
		generator.writeString(DateTimeFormatter.ISO_OFFSET_TIME.format(value));
	}
}
