package com.guicedee.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;

/**
 * Lenient primitive integer deserializer that accepts numeric strings with decimals.
 */
public class StringToIntRelaxed
		extends JsonDeserializer
{
	/**
	 * Deserializes a primitive integer from a JSON scalar value.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return the parsed integer value
	 * @throws IOException when parsing fails
	 */
	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		String value = p.getValueAsString();
		return convert(value);
	}

	/**
	 * Converts a string to a primitive integer, truncating any decimal portion.
	 *
	 * @param value the input string
	 * @return the parsed integer value, or 0 when the input is empty
	 */
	public int convert( String value)
	{
		if (Strings.isNullOrEmpty(value))
		{
			return 0;
		}
		value = value.trim();
		double d = Double.parseDouble(value);
		return (int) d;
	}
}
