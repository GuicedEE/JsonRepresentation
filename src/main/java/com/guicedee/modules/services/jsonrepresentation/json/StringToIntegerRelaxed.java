package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.google.common.base.Strings;

import java.io.IOException;

/**
 * Lenient integer deserializer that accepts numeric strings with decimals.
 */
public class StringToIntegerRelaxed extends ValueDeserializer<Integer> {
    /**
     * Deserializes an {@link Integer} from a JSON scalar value.
     *
     * @param p the parser positioned at a scalar value
     * @param ctxt the deserialization context
     * @return the parsed integer, or null when the input is empty
     * @throws IOException when parsing fails
     */
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) {
        String value = p.getValueAsString();
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return convert(value);
    }

    /**
     * Converts a string to an integer, truncating any decimal portion.
     *
     * @param value the input string
     * @return the parsed integer, or null when the input is empty
     */
    public Integer convert(  String value)
    {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        value = value.trim();
        double d = Double.parseDouble(value);
        return (int) d;
    }
}
