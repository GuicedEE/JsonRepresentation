package com.guicedee.modules.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.guicedee.modules.services.jsonrepresentation.json.LocalDateTimeDeserializer.formats;
import static com.guicedee.modules.services.jsonrepresentation.json.StaticStrings.STRING_0;
import static com.guicedee.modules.services.jsonrepresentation.json.StaticStrings.STRING_NULL;

/**
 * Lenient {@link LocalTime} deserializer that accepts multiple time formats.
 */
public class LocalTimeDeserializer
		extends JsonDeserializer<LocalTime>
{
	/**
	 * Deserializes a {@link LocalTime} from a JSON scalar value.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return the parsed time, or null when input is empty
	 * @throws IOException when parsing fails
	 */
	@Override
	public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		String name = p.getValueAsString();
		return convert(name);
	}
	
	/**
	 * Converts a string into a {@link LocalTime} by trying multiple formats.
	 *
	 * @param value the input string
	 * @return the parsed time, or null when input is empty
	 */
	public LocalTime convert(String value)
	{
		if (Strings.isNullOrEmpty(value) || STRING_NULL.equalsIgnoreCase(value) || STRING_0.equals(value))
		{
			return null;
		}
		LocalTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = LocalTime.parse(value, format);
				if (time != null)
				{
					break;
				}
			}
			catch (DateTimeParseException p)
			{
			
			}
		}
		
		return time;
	}
}
