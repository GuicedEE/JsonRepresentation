import com.guicedee.client.services.lifecycle.IGuiceModule;

/**
 * Provides JSON representation utilities, Jackson configuration, and Guice bindings
 * for consistent serialization and deserialization across the application.
 */
module com.guicedee.jsonrepresentation {
	exports com.guicedee.modules.services.jsonrepresentation;
	exports com.guicedee.modules.services.jsonrepresentation.json;
    exports com.guicedee.vertx.spi.json;
	
	requires transitive tools.jackson.databind;
	requires transitive tools.jackson.core;

	requires com.guicedee.client;
	requires org.apache.commons.lang3;
	
	requires static lombok;
	requires java.logging;
	requires com.fasterxml.jackson.annotation;

	requires static jakarta.inject;
	
	provides IGuiceModule with com.guicedee.modules.services.jsonrepresentation.implementations.ObjectMapperBinder;
    provides com.guicedee.client.services.IJsonCodec with com.guicedee.modules.services.jsonrepresentation.implementations.JacksonJsonCodec;
    provides io.vertx.core.spi.JsonFactory with com.guicedee.vertx.spi.json.GuicedVertxJsonFactory;
}
