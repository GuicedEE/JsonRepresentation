package com.guicedee.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;

import static com.guicedee.services.jsonrepresentation.json.StaticStrings.STRING_EMPTY;
import static java.time.temporal.ChronoUnit.*;

/**
 * Serializes a {@link Duration} to a compact HHMMSS-style integer value.
 */
public class DurationToInteger
		extends JsonSerializer<Duration>
{
	private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
	static {
		numberFormat.setMaximumFractionDigits(0);
		numberFormat.setMinimumIntegerDigits(1);
	}
	/**
	 * Writes the duration as a compact integer representation.
	 *
	 * @param value the duration to serialize
	 * @param gen the JSON generator
	 * @param serializers the serializer provider
	 * @throws IOException when writing fails
	 */
	@Override
	public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException
	{
		if(value == null)
			return ;
		gen.writeNumber(convert(value));
	}

	/**
	 * Converts a duration to a HHMMSS-style integer value.
	 *
	 * @param value the duration to convert
	 * @return the compact integer representation
	 */
	public Integer convert(Duration value)
	{
		String intNumber = numberFormat.format(value.get(HOURS)) + STRING_EMPTY +
				numberFormat.format(value.get(MINUTES)) + STRING_EMPTY +
				numberFormat.format(value.get(SECONDS));
		return Integer.parseInt(intNumber);
	}
}
