package org.igor.homeassistant.temperature;

import org.igor.homeassistant.model.Temperature;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
@ConfigurationProperties(prefix = "temperature.search")
public class SearchTemperatureService {
    private String defaultGroup;

    public Temperature[] searchTemperatures(String name){
        if (name == null || name.isBlank()) {
            name = defaultGroup;
        }
        Temperature temp = new Temperature(1L);
        temp.setName(name);
        temp.setGroup("grouping");
        temp.setReadingDate(Calendar.getInstance().getTime());
        temp.setReading(17.5F);
        Temperature[] readings = {temp};
        return readings;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}
