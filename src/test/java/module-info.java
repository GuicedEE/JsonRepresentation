module com.guicedee.jsonrepresentation.test {
    requires com.guicedee.jsonrepresentation;
    requires org.junit.jupiter.api;
    requires tools.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens com.guicedee.modules.services.jsonrepresentation.test to org.junit.platform.commons, tools.jackson.databind;
}

