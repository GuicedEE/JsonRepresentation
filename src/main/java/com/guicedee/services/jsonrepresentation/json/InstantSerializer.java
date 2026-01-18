package com.guicedee.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Serializes {@link Instant} values as ISO-8601 strings.
 */
public class InstantSerializer
		extends JsonSerializer<Instant>
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
	public void serialize(Instant value, JsonGenerator generator, SerializerProvider provider) throws IOException
	{
		generator.writeString(DateTimeFormatter.ISO_INSTANT.format(value));
	}
}
