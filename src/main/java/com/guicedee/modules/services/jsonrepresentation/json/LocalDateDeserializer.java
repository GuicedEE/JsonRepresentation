package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.google.common.base.Strings;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;

import static com.guicedee.modules.services.jsonrepresentation.json.StaticStrings.*;

/**
 * Lenient {@link LocalDate} deserializer that accepts multiple date formats.
 */
@Log
public class LocalDateDeserializer
		extends ValueDeserializer<LocalDate>
{
	/**
	 * Deserializes a {@link LocalDate} from a JSON scalar value.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return the parsed date, or null when input is empty
	 * @throws IOException when parsing fails
	 */
	@Override
	public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	/**
	 * Converts a string into a {@link LocalDate} using the shared date-time parser.
	 *
	 * @param value the input string
	 * @return the parsed date, or null when input is empty
	 */
	public LocalDate convert(String value)
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
		if (value.length() == 7)
		{
			value = new StringBuilder(value).insert(value.length() - 1, 0)
			                              .toString();
		}
        LocalDate time = new LocalDateTimeDeserializer().convert(value).toLocalDate();
		if (time == null)
		{
			log.log(Level.WARNING,"Unable to determine local date from string - [" + value + "]");
		}
		return time;
	}
}
