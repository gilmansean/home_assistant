package org.igor.homeassistant.model;

import org.igor.homeassistant.annotations.HomeAssistantDAO;

import java.util.Date;
@HomeAssistantDAO(location = "temperature.data",
                  primaryIdentifiers = {"id"})
public class Temperature {
    private final Long id;
    private String name;
    private String group;
    private Scale readingScale = Scale.FAHRENHEIT;
    private Date readingDate;
    private Float reading;

    //constructor so this can be used in a search
    public Temperature() {
        this.id = -1L;
    }

    public Temperature(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Scale getReadingScale() {
        return readingScale;
    }

    public void setReadingScale(Scale readingScale) {
        this.readingScale = readingScale;
    }

    public Date getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(Date readingDate) {
        this.readingDate = readingDate;
    }

    public Float getReading() {
        return reading;
    }

    public void setReading(Float reading) {
        this.reading = reading;
    }

    public enum Scale {
        FAHRENHEIT,
        CELSIUS
    }
}
