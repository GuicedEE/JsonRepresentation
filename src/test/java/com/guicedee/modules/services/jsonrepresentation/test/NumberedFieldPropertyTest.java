package com.guicedee.modules.services.jsonrepresentation.test;

import com.guicedee.modules.services.jsonrepresentation.IJsonRepresentation;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reproduces a reported regression: POJO fields whose names end in a digit
 * (e.g. {@code image1}, {@code image2}, {@code image3}) allegedly stop being
 * serialized after the Jackson 3 migration.
 */
class NumberedFieldPropertyTest
{
    /**
     * A nested DTO, mirroring the reported {@code MaterialDTO}.
     */
    public static class MaterialDTO
    {
        private String url;

        public MaterialDTO()
        {
        }

        public MaterialDTO(String url)
        {
            this.url = url;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }

        @Override
        public boolean equals(Object o)
        {
            return o instanceof MaterialDTO m && Objects.equals(url, m.url);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(url);
        }
    }

    /**
     * A POJO with digit-suffixed fields plus conventional getters/setters, matching
     * the reported shape.
     */
    public static class GalleryDTO
    {
        private MaterialDTO image1;
        private MaterialDTO image2;
        private MaterialDTO image3;

        public MaterialDTO getImage1() { return image1; }
        public void setImage1(MaterialDTO image1) { this.image1 = image1; }

        public MaterialDTO getImage2() { return image2; }
        public void setImage2(MaterialDTO image2) { this.image2 = image2; }

        public MaterialDTO getImage3() { return image3; }
        public void setImage3(MaterialDTO image3) { this.image3 = image3; }
    }

    @Test
    void numberedFieldsAreSerialized()
    {
        ObjectMapper mapper = IJsonRepresentation.getObjectMapper();

        GalleryDTO gallery = new GalleryDTO();
        gallery.setImage1(new MaterialDTO("a.png"));
        gallery.setImage2(new MaterialDTO("b.png"));
        gallery.setImage3(new MaterialDTO("c.png"));

        String json = mapper.writeValueAsString(gallery);

        assertAll(
                () -> assertTrue(json.contains("\"image1\""), () -> "image1 missing in: " + json),
                () -> assertTrue(json.contains("\"image2\""), () -> "image2 missing in: " + json),
                () -> assertTrue(json.contains("\"image3\""), () -> "image3 missing in: " + json),
                () -> assertTrue(json.contains("a.png"), () -> "image1 url missing in: " + json),
                () -> assertTrue(json.contains("b.png"), () -> "image2 url missing in: " + json),
                () -> assertTrue(json.contains("c.png"), () -> "image3 url missing in: " + json)
        );
    }

    @Test
    void numberedFieldsRoundTrip() throws Exception
    {
        ObjectMapper mapper = IJsonRepresentation.getObjectMapper();

        GalleryDTO gallery = new GalleryDTO();
        gallery.setImage1(new MaterialDTO("a.png"));
        gallery.setImage2(new MaterialDTO("b.png"));
        gallery.setImage3(new MaterialDTO("c.png"));

        String json = mapper.writeValueAsString(gallery);
        GalleryDTO back = mapper.readValue(json, GalleryDTO.class);

        assertEquals(new MaterialDTO("a.png"), back.getImage1());
        assertEquals(new MaterialDTO("b.png"), back.getImage2());
        assertEquals(new MaterialDTO("c.png"), back.getImage3());
    }

    /**
     * Field-only POJO (no getters/setters) — relies purely on field visibility ANY.
     */
    public static class FieldOnlyGalleryDTO
    {
        private MaterialDTO image1;
        private MaterialDTO image2;
        private MaterialDTO image3;
    }

    @Test
    void numberedFieldsWithoutAccessorsAreSerialized()
    {
        ObjectMapper mapper = IJsonRepresentation.getObjectMapper();

        FieldOnlyGalleryDTO gallery = new FieldOnlyGalleryDTO();
        gallery.image1 = new MaterialDTO("a.png");
        gallery.image2 = new MaterialDTO("b.png");
        gallery.image3 = new MaterialDTO("c.png");

        String json = mapper.writeValueAsString(gallery);
        assertAll(
                () -> assertTrue(json.contains("\"image1\""), () -> "image1 missing in: " + json),
                () -> assertTrue(json.contains("\"image2\""), () -> "image2 missing in: " + json),
                () -> assertTrue(json.contains("\"image3\""), () -> "image3 missing in: " + json)
        );
    }

    /**
     * Confirms the most likely real cause of "missing" fields: {@code NON_NULL} inclusion
     * omits null-valued properties (numbered or not) from the output.
     */
    @Test
    void nullNumberedFieldsAreOmittedByNonNullInclusion()
    {
        ObjectMapper mapper = IJsonRepresentation.getObjectMapper();

        GalleryDTO gallery = new GalleryDTO();
        gallery.setImage1(new MaterialDTO("a.png"));
        // image2 and image3 left null

        String json = mapper.writeValueAsString(gallery);
        assertTrue(json.contains("\"image1\""), () -> "image1 should render: " + json);
        assertFalse(json.contains("\"image2\""), () -> "null image2 should be omitted (NON_NULL): " + json);
        assertFalse(json.contains("\"image3\""), () -> "null image3 should be omitted (NON_NULL): " + json);
    }
}


