package com.guicedee.modules.services.jsonrepresentation.test;

import com.google.inject.TypeLiteral;
import com.guicedee.client.services.websocket.WebSocketMessageReceiver;
import com.guicedee.client.utils.GlobalProperties;
import com.guicedee.client.utils.JsonRepresentation;
import com.guicedee.modules.services.jsonrepresentation.IJsonRepresentation;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonProviderTest {
    static class Dto { private String name; Dto() { } Dto(String name) { this.name = name; } }
    @Test void serviceProvidesGenericRoundTripsAndIndependentCopies() {
        var codec = JsonRepresentation.require();
        var type = new TypeLiteral<List<Dto>>() { }.getType();
        List<Dto> original = new ArrayList<>(List.of(new Dto("first")));
        List<Dto> copy = codec.copy(original, type);
        assertEquals("first", copy.getFirst().name);
        copy.getFirst().name = "second";
        assertEquals("first", original.getFirst().name);
        Dto converted = codec.convert(Map.of("name", "mapped"), Dto.class);
        assertEquals("mapped", converted.name);
        codec.update(converted, new Dto("updated"));
        assertEquals("updated", converted.name);
    }
    @Test void receiverUnknownPropertiesRetainTheirAnySetter() {
        WebSocketMessageReceiver<?> receiver = JsonRepresentation.require().read("{\"action\":\"go\",\"extra\":\"payload\"}", WebSocketMessageReceiver.class);
        assertEquals("go", receiver.getAction());
        assertEquals("payload", receiver.getData().get("extra"));
    }
    @Test void globalPropertiesUseInstalledRepresentation() {
        var properties = new GlobalProperties();
        properties.addProperty("group", "value", "quoted\"value");
        var tree = IJsonRepresentation.getObjectMapper().readTree(properties.toString());
        assertEquals("quoted\"value", tree.path("globalProperties").path("group").path("value").asText());
    }
    @Test void serviceFilesMatchModuleProviders() throws Exception {
        var module = IJsonRepresentation.class.getModule();
        for (var provider : module.getDescriptor().provides()) {
            try (var resource = module.getResourceAsStream("META-INF/services/" + provider.service())) {
                assertNotNull(resource, provider.service());
                var lines = new String(resource.readAllBytes(), StandardCharsets.UTF_8).lines()
                        .map(line -> line.split("#", 2)[0].trim()).filter(line -> !line.isEmpty()).toList();
                assertEquals(Set.copyOf(provider.providers()), Set.copyOf(lines), provider.service());
            }
        }
    }
}
