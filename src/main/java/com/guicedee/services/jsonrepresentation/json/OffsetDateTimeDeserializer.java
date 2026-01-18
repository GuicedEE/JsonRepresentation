package com.guicedee.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;

import static com.guicedee.services.jsonrepresentation.json.LocalDateTimeDeserializer.formats;
import static com.guicedee.services.jsonrepresentation.json.StaticStrings.*;

/**
 * Lenient {@link OffsetDateTime} deserializer that accepts multiple formats.
 */
@Log
public class OffsetDateTimeDeserializer
		extends JsonDeserializer<OffsetDateTime>
{
	/**
	 * Deserializes an {@link OffsetDateTime} from a JSON scalar value.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return the parsed offset date-time, or null when input is empty
	 * @throws IOException when parsing fails
	 */
	@Override
	public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String name = p.getValueAsString();
		return convert(name);
	}

	/**
	 * Converts a string into an {@link OffsetDateTime}, falling back to UTC when needed.
	 *
	 * @param value the input string
	 * @return the parsed offset date-time, or null when input is empty
	 */
	public OffsetDateTime convert(String value)
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
		OffsetDateTime time = null;
		for (DateTimeFormatter format : formats)
		{
			try
			{
				time = OffsetDateTime.parse(value, format);
				break;
			}
			catch (DateTimeParseException dtpe)
			{
				//try the next one
			}
		}
		if (time == null)
		{
			
			LocalDateTime convert = new LocalDateTimeDeserializer().convert(value);
			if (convert != null)
			{
				time = convertToUTCDateTime(convert);
			}else
			{
				log.log(Level.WARNING, "Unable to determine offset datetime from string - [" + value + "]");
			}
		}
		return time;
	}

	/**
	 * Converts a local date-time to UTC using the system default zone.
	 *
	 * @param ldt the local date-time
	 * @return the UTC offset date-time, or null when input is null
	 */
	private OffsetDateTime convertToUTCDateTime(LocalDateTime ldt)
	{
		if (ldt == null)
		{
			return null;
		}
		ZonedDateTime zonedDateTime = ldt.atZone(ZoneId.systemDefault());
		ZonedDateTime utcZonedDateTime = zonedDateTime.withZoneSameLocal(ZoneId.of("UTC"));
		OffsetDateTime offsetDateTime = utcZonedDateTime.toOffsetDateTime();
		return offsetDateTime;
	}
	
}
