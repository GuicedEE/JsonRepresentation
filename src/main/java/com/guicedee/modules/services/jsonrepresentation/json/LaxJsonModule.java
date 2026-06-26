package com.guicedee.modules.services.jsonrepresentation.json;

import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;
import com.guicedee.modules.services.jsonrepresentation.json.mapkeys.LocalDateDeserializerKey;
import com.guicedee.modules.services.jsonrepresentation.json.mapkeys.LocalDateTimeDeserializerKey;
import com.guicedee.modules.services.jsonrepresentation.json.mapkeys.OffsetDateTimeDeserializerKey;

import java.io.IOException;
import java.time.*;

/**
 * Jackson module that registers relaxed serializers and deserializers for
 * common Java time types and lenient scalar parsing.
 */
public class LaxJsonModule extends SimpleModule
{
	/**
	 * Creates the module with time-related serializers, deserializers, and key deserializers.
	 */
	public LaxJsonModule()
	{
		super("GuicedTimeHandler", Version.unknownVersion());
		
		addDeserializer(Boolean.class, new StringToBoolean())
				.addDeserializer(boolean.class, new ValueDeserializer()
				{
					@Override
					public Object deserialize(JsonParser p, DeserializationContext ctxt)
					{
						return new StringToBool().deserialize(p, ctxt);
					}
				})
				.addDeserializer(int.class, new ValueDeserializer()
				{
					@Override
					public Object deserialize(JsonParser p, DeserializationContext ctxt)
					{
						return new StringToIntRelaxed().deserialize(p, ctxt);
					}
				})
				.addDeserializer(Integer.class, new ValueDeserializer()
				{
					@Override
					public Object deserialize(JsonParser p, DeserializationContext ctxt)
					{
						return new StringToIntegerRelaxed().deserialize(p, ctxt);
					}
				})
				.addDeserializer(Duration.class, new StringToDurationTimeSeconds())
				.addDeserializer(LocalDate.class, new LocalDateDeserializer())
				.addDeserializer(LocalTime.class, new LocalTimeDeserializer())
				.addSerializer(LocalTime.class, new LocalTimeSerializer())
				.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
				.addDeserializer(Integer.class, new StringToIntegerRelaxed())
				.addSerializer(LocalDate.class, new LocalDateSerializer())
				.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
				.addSerializer(Duration.class, new DurationToString())
				.addDeserializer(Instant.class, new InstantDeserializer())
				.addSerializer(Instant.class, new InstantSerializer())
				.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer())
				.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer())
				.addDeserializer(OffsetTime.class, new OffsetTimeDeserializer())
				.addSerializer(OffsetTime.class, new OffsetTimeSerializer())
				.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer())
				
				.addKeyDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializerKey())
				.addKeyDeserializer(LocalDateTime.class, new LocalDateTimeDeserializerKey())
				.addKeyDeserializer(LocalDate.class, new LocalDateDeserializerKey())
		
		;
	}
	
}
