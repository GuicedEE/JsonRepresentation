package com.guicedee.services.jsonrepresentation.json.mapkeys;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.guicedee.services.jsonrepresentation.json.LocalDateDeserializer;

import java.io.IOException;

/**
 * Key deserializer for {@link java.time.LocalDate} map keys.
 */
public class LocalDateDeserializerKey
		extends KeyDeserializer
{
	/**
	 * Converts a JSON object key into a {@link java.time.LocalDate}.
	 *
	 * @param key the map key string
	 * @param ctxt the deserialization context
	 * @return the parsed local date
	 * @throws IOException when parsing fails
	 */
	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException
	{
		return new LocalDateDeserializer().convert(key);
	}
}
