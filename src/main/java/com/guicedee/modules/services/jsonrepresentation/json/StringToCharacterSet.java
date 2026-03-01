package com.guicedee.modules.services.jsonrepresentation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Deserializes a JSON string into a set of characters.
 */
public class StringToCharacterSet
		extends JsonDeserializer<Set<Character>>
{
	/**
	 * Converts the JSON string into a linked set of characters.
	 *
	 * @param p the parser positioned at a scalar value
	 * @param ctxt the deserialization context
	 * @return a set of characters in encounter order
	 * @throws IOException when parsing fails
	 */
	@Override
	public Set<Character> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		Set<Character> chars = new LinkedHashSet<>();
		String value = p.getValueAsString();
		if (Strings.isNullOrEmpty(value))
		{
			return chars;
		}
		value = StringEscapeUtils.unescapeJava(value);
		value.chars().forEach(a->chars.add((char)a));
		
		return chars;
	}
}
