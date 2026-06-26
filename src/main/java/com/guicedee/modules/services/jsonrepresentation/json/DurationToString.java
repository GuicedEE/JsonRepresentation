package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.time.Duration;

/**
 * Serializes {@link Duration} values to their ISO-8601 string form.
 */
public class DurationToString
		extends ValueSerializer<Duration>
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
	public void serialize(Duration value, JsonGenerator gen, SerializationContext serializers)
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
