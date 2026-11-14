package com.guicedee.modules.services.jsonrepresentation.implementations;

import com.guicedee.client.services.IJsonCodec;
import java.lang.reflect.Type;

/** Uses the same configured mapper as the JSON representation API and Guice bindings. */
public final class JacksonJsonCodec implements IJsonCodec {
    public String write(Object value) { return ObjectMapperBinder.getObjectMapper().writeValueAsString(value); }
    public <T> T read(String json, Type type) {
        var mapper = ObjectMapperBinder.getObjectMapper();
        return mapper.readValue(json, mapper.getTypeFactory().constructType(type));
    }
    public <T> T convert(Object value, Type type) {
        var mapper = ObjectMapperBinder.getObjectMapper();
        return mapper.convertValue(value, mapper.getTypeFactory().constructType(type));
    }
    public void update(Object target, Object source) {
        var mapper = ObjectMapperBinder.getObjectMapper();
        mapper.readerForUpdating(target).readValue(mapper.writeValueAsString(source));
    }
    public <T> T copy(T value, Type type) {
        if (value == null) return null;
        var mapper = ObjectMapperBinder.getObjectMapper();
        return mapper.readValue(mapper.writeValueAsBytes(value), mapper.getTypeFactory().constructType(type));
    }
}
