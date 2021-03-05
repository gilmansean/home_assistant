package org.igor.homeassistant.dataaccess;

import org.igor.homeassistant.annotations.HomeAssistantDAO;

@HomeAssistantDAO(location = "testData", primaryIdentifiers = {"id"})
public class TestModel {
    private String id;
    private String name;

    public TestModel() {
    }

    public TestModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
