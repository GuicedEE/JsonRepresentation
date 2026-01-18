package com.guicedee.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

/**
 * Serializes {@link Duration} values to their ISO-8601 string form.
 */
public class DurationToString
		extends JsonSerializer<Duration>
{
	/**
	 * Writes the duration as an ISO-8601 string.
	 *
	 * @param value the duration to serialize
	 * @param gen the JSON generator
	 * @param serializers the serializer provider
	 * @throws IOException when writing fails
	 */
	@Override
	public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException
	{
		if(value == null)
			return ;
		gen.writeString(convert(value));
	}

	/**
	 * Converts a duration to its ISO-8601 string representation.
	 *
	 * @param value the duration to convert
	 * @return the ISO-8601 duration string
	 */
	public String convert( Duration value)
	{
		return value.toString();
	}
}
