package com.guicedee.modules.services.jsonrepresentation.test;

import com.guicedee.modules.services.jsonrepresentation.IJsonRepresentation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates the Jackson 3 migration of the JSON representation layer at runtime
 * (round-trip serialization/deserialization, custom date handling, and null omission).
 */
class JsonRepresentationTest
{
    @Test
    void roundTripPreservesScalarsCollectionsAndDates()
    {
        SamplePojo p = new SamplePojo();
        p.setName("hello");
        p.setCount(5);
        p.setActive(true);
        p.setDate(LocalDate.of(2026, 6, 25));
        p.setTimestamp(LocalDateTime.of(2026, 6, 25, 10, 30, 0));
        p.setTags(List.of("a", "b"));

        String json = p.toJson();
        assertNotNull(json);

        SamplePojo back = new SamplePojo().fromJson(json);
        assertEquals("hello", back.getName());
        assertEquals(5, back.getCount());
        assertTrue(back.isActive());
        assertEquals(LocalDate.of(2026, 6, 25), back.getDate());
        assertEquals(LocalDateTime.of(2026, 6, 25, 10, 30, 0), back.getTimestamp());
        assertEquals(List.of("a", "b"), back.getTags());
        assertEquals(p, back);
    }

    @Test
    void localDateSerializesUsingConfiguredPattern()
    {
        SamplePojo p = new SamplePojo();
        p.setDate(LocalDate.of(2026, 6, 25));
        String json = p.toJson(true);
        assertTrue(json.contains("2026-06-25"), () -> "Expected ISO date in: " + json);
    }

    @Test
    void compactOutputHasNoIndentation()
    {
        SamplePojo p = new SamplePojo();
        p.setName("x");
        String tiny = p.toJson(true);
        assertFalse(tiny.contains("\n"), () -> "Compact JSON should not contain newlines: " + tiny);
    }

    @Test
    void nullPropertiesAreOmitted()
    {
        SamplePojo p = new SamplePojo();
        p.setCount(1);
        String json = p.toJson(true);
        assertFalse(json.contains("name"), () -> "NON_NULL inclusion should omit null name: " + json);
    }

    @Test
    void staticFromStringParsesLeniently() throws Exception
    {
        String json = "{\"name\":\"abc\",\"count\":\"3\"}";
        SamplePojo p = IJsonRepresentation.From(json, SamplePojo.class);
        assertEquals("abc", p.getName());
        assertEquals(3, p.getCount());
    }
}

