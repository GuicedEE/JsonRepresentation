package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.google.common.base.Strings;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.logging.Level;

import static com.guicedee.modules.services.jsonrepresentation.json.StaticStrings.*;

/**
 * Lenient {@link OffsetTime} deserializer that accepts ISO-8601 time strings.
 */
@Log
public class OffsetTimeDeserializer
		extends ValueDeserializer<OffsetTime>
{
	private static final DateTimeFormatter[] formats = new DateTimeFormatter[]
			                                                   {
					                                                   new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_OFFSET_TIME)
					                                                                                 .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
					                                                                                 .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
					                                                                                 .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
							                                                   .toFormatter()
			                                                   };

	/**
	 * Deserializes an {@link OffsetTime} from a JSON scalar value.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return the parsed offset time, or null when input is empty
	 * @throws IOException when parsing fails
	 */
	@Override
	public OffsetTime deserialize(JsonParser p, DeserializationContext ctxt)
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	/**
	 * Converts a string into an {@link OffsetTime}.
	 *
	 * @param value the input string
	 * @return the parsed offset time, or null when input is empty
	 */
	public OffsetTime convert(String value)
	{
		if (Strings.isNullOrEmpty(value) || STRING_NULL.equalsIgnoreCase(value) || STRING_0.equals(value))
		{
			return null;
		}
		if (value.contains(E))
		{
			value = value.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY)
			             .substring(0, value.indexOf(E) - 1);
		}
		OffsetTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = OffsetTime.parse(value, format);
				break;
			}
			catch (DateTimeParseException dtpe)
			{
				//try the next one
			}
		}
		if (time == null)
		{
			log.log(Level.WARNING,"Unable to determine offset time from string - [" + value + "]");

		}
		return time;
	}
}
