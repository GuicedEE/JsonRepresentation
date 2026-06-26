package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;

import static com.guicedee.modules.services.jsonrepresentation.json.StaticStrings.*;

/**
 * Lenient {@link Duration} deserializer that accepts HHmmss numeric strings
 * or ISO-8601 duration values.
 */
public class StringToDurationTimeSeconds extends ValueDeserializer<Duration> {
    private static final NumberFormat nf = NumberFormat.getInstance();

    static {
        nf.setMinimumIntegerDigits(2);
    }

    /**
     * Deserializes a {@link Duration} from a JSON scalar value.
     *
     * @param p the parser positioned at a scalar value
     * @param ctxt the deserialization context
     * @return the parsed duration, or null when input is empty
     * @throws IOException when parsing fails
     */
    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) {
        String name = p.getValueAsString();
        return convert(name);
    }

    /**
     * Converts a string to a {@link Duration}. Supports HHmmss numeric values,
     * ISO-8601 duration strings, and scientific-notation numeric input.
     *
     * @param value the input string
     * @return the parsed duration, or null when input is empty
     */
    public Duration convert(String value)
    {
        if (Strings.isNullOrEmpty(value) || STRING_NULL.equals(value) || STRING_0.equals(value)) {
            return null;
        }
        if (value.contains(E)) {
            value = value.replaceAll(STRING_DOT_ESCAPED, STRING_EMPTY).substring(0, value.indexOf(E) - 1);
        }

        if (value.contains(STRING_DOT)) {
            double d = Double.parseDouble(value);
            value = String.valueOf((int) d);
        }

        value = value.trim();
        if (value.length() == 4) {
            return new StringToDurationTime().convert(value);
        }

        if (value.length() < 6) {
            value = StringUtils.leftPad(value, value.length() + 1, STRING_0);
        }
        if (!value.contains(P)) {
            //Numeric
            int hours = Integer.parseInt(value.substring(0, 2));
            int minutes = Integer.parseInt(value.substring(2, 4));
            int seconds = Integer.parseInt(value.substring(4, 6));
            return Duration.parse(STRING_DURATION_TIME + nf.format(hours) + H + nf.format(minutes) + M + nf.format(seconds) + S);
        } else {
            if(value.indexOf(P) != 0)
            {
                value = value.substring(value.indexOf(P));
            }
            return Duration.parse(value);
        }
    }

}
