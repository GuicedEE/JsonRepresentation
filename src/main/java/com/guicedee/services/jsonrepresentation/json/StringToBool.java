package com.guicedee.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Lenient primitive boolean deserializer that defaults to false for unknown values.
 */
public class StringToBool
		extends JsonDeserializer
{
	/**
	 * Deserializes a primitive boolean from a JSON scalar value.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return the parsed boolean value
	 * @throws IOException when parsing fails
	 */
	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		String value = p.getValueAsString();
		return convert(value);
	}

	/**
	 * Converts a string value into a primitive boolean.
	 *
	 * @param value the input string
	 * @return true for truthy values, false otherwise
	 */
	public boolean convert(String value)
	{
		Boolean bValue = new StringToBoolean().convert(value);
		if (bValue == null)
		{
			return false;
		}
		else
		{
			return bValue;
		}
	}
}
