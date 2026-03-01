package com.guicedee.modules.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;

/**
 * Lenient boolean deserializer that accepts multiple string forms.
 */
public class StringToBoolean
		extends JsonDeserializer<Boolean>
{
	public static boolean nullable = true;

	/**
	 * Deserializes a boolean from a string value in the JSON payload.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return the parsed Boolean value
	 * @throws IOException when parsing fails
	 */
	@Override
	public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		return convert(p.getValueAsString());
	}

	/**
	 * Converts a string value into a Boolean, returning null for unknown values
	 * when {@link #nullable} is enabled.
	 *
	 * @param value the input string
	 * @return true, false, or null depending on input and configuration
	 */
	public Boolean convert(String value)
	{
		if (Strings.isNullOrEmpty(value))
		{
			return null;
		}
		value = value.trim().toLowerCase();
		switch (value)
		{
			case "1":
			case "1.0":
			case "y":
			case "yes":
			case "on":
			case "true":
				return true;
			case "0":
			case "0.0":
			case "no":
			case "off":
			case "n":
			case "false":
				return false;
		}
		if(nullable)
			return null;
		else return false;
	}
}
