package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;

import static com.guicedee.modules.services.jsonrepresentation.json.StaticStrings.STRING_EMPTY;
import static java.time.temporal.ChronoUnit.*;

/**
 * Serializes a {@link Duration} to a compact HHMMSS-style integer value.
 */
public class DurationToInteger
		extends ValueSerializer<Duration>
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
	public void serialize(Duration value, JsonGenerator gen, SerializationContext serializers)
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
