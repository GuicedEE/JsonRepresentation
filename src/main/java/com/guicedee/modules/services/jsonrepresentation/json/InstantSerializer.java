package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@link Instant} values as ISO-8601 strings.
 */
public class InstantSerializer
		extends ValueSerializer<Instant>
{
	public InstantSerializer()
	{
	}

	/**
	 * Writes the instant as an ISO-8601 string.
	 *
	 * @param value the instant to serialize
	 * @param generator the JSON generator
	 * @param provider the serializer provider
	 * @throws IOException when writing fails
	 */
	@Override
	public void serialize(Instant value, JsonGenerator generator, SerializationContext provider)
	{
		generator.writeString(DateTimeFormatter.ISO_INSTANT.format(value));
	}
}
