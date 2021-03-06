package org.igor.homeassistant.dataaccess.model;

import org.igor.homeassistant.annotations.HomeAssistantDAO;

@HomeAssistantDAO(location = "threshold", primaryIdentifiers = {"id"})
public class Threshold {
    private String id;
    private String associatedId;
    private String name;
    private Float maxLimit;
    private Float minLimit;
    private Boolean alertOver = false;
    private Boolean alertUnder = false;
    private String email;

    public Threshold(String id) {
        this.id = id;
    }

    public Threshold() {
        this.id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(String associatedId) {
        this.associatedId = associatedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(Float maxLimit) {
        this.maxLimit = maxLimit;
    }

    public Float getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(Float minLimit) {
        this.minLimit = minLimit;
    }

    public Boolean getAlertOver() {
        return alertOver;
    }

    public void setAlertOver(Boolean alertOver) {
        this.alertOver = alertOver;
    }

    public Boolean getAlertUnder() {
        return alertUnder;
    }

    public void setAlertUnder(Boolean alertUnder) {
        this.alertUnder = alertUnder;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
