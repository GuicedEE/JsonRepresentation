package com.guicedee.modules.services.jsonrepresentation.test;

import com.guicedee.modules.services.jsonrepresentation.IJsonRepresentation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Simple POJO used to exercise the Jackson 3 round-trip behaviour of
 * {@link IJsonRepresentation}.
 */
public class SamplePojo implements IJsonRepresentation<SamplePojo>
{
    private String name;
    private int count;
    private boolean active;
    private LocalDate date;
    private LocalDateTime timestamp;
    private List<String> tags;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SamplePojo that)) return false;
        return count == that.count
                && active == that.active
                && Objects.equals(name, that.name)
                && Objects.equals(date, that.date)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, count, active, date, timestamp, tags);
    }
}

