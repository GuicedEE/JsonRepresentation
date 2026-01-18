package com.guicedee.services.jsonrepresentation.json.mapkeys;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.guicedee.services.jsonrepresentation.json.OffsetDateTimeDeserializer;

import java.io.IOException;

/**
 * Key deserializer for {@link java.time.OffsetDateTime} map keys.
 */
public class OffsetDateTimeDeserializerKey
		extends KeyDeserializer
{
	/**
	 * Converts a JSON object key into a {@link java.time.OffsetDateTime}.
	 *
	 * @param key the map key string
	 * @param ctxt the deserialization context
	 * @return the parsed offset date-time
	 * @throws IOException when parsing fails
	 */
	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException
	{
		return new OffsetDateTimeDeserializer().convert(key);
	}
}
