package com.guicedee.modules.services.jsonrepresentation.json.mapkeys;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.guicedee.modules.services.jsonrepresentation.json.LocalDateTimeDeserializer;

import java.io.IOException;

/**
 * Key deserializer for {@link java.time.LocalDateTime} map keys.
 */
public class LocalDateTimeDeserializerKey
		extends KeyDeserializer
{
	/**
	 * Converts a JSON object key into a {@link java.time.LocalDateTime}.
	 *
	 * @param key the map key string
	 * @param ctxt the deserialization context
	 * @return the parsed local date-time
	 * @throws IOException when parsing fails
	 */
	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException
	{
		return new LocalDateTimeDeserializer().convert(key);
	}
}
