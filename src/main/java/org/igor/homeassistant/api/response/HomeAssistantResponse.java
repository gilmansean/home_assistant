package org.igor.homeassistant.api.response;

public class HomeAssistantResponse<R> {
    private R data;

    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }
}
