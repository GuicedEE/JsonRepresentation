package com.guicedee.modules.services.jsonrepresentation.json;

import com.fasterxml.jackson.annotation.*;
import com.guicedee.client.services.websocket.WebSocketMessageReceiver;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.MapperBuilder;

/** Serialization metadata lives with the optional JSON implementation, not core DTOs. */
public final class CoreJsonMixins {
    private CoreJsonMixins() { }
    @JsonIgnoreProperties(ignoreUnknown = true)
    abstract static class Receiver {
        @JsonAnySetter public abstract void add(String key, String value);
    }
    @JsonSerialize(using = tools.jackson.databind.ser.std.NullSerializer.class)
    abstract static class Publisher { }

    public static void configure(MapperBuilder<?, ?> builder) {
        builder.addMixIn(WebSocketMessageReceiver.class, Receiver.class);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) loader = CoreJsonMixins.class.getClassLoader();
        try {
            builder.addMixIn(Class.forName("com.guicedee.vertx.VertxEventPublisher", false, loader), Publisher.class);
        } catch (ClassNotFoundException absent) {
            // The representation library also works without the GuicedEE Vert.x integration.
        }
    }
}
