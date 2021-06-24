package org.igor.homeassistant.service.threshold;

import org.igor.homeassistant.annotations.HomeAssistantThreshold;
import org.igor.homeassistant.dataaccess.ObjectDataAccess;
import org.springframework.stereotype.Component;

@Component
public class ThresholdService {
    private ObjectDataAccess objectDataAccess;

    public ThresholdService(ObjectDataAccess objectDataAccess) {
        this.objectDataAccess = objectDataAccess;
    }

    /*
    TODO:SMG Need to have a threshold checking method.  Something maybe that takes in generics and uses an annotation to figure
    out what to check threshold on?
     */
    public <M> void monitorThreshold(M monitor) {
        HomeAssistantThreshold hat = monitor.getClass()
                                            .getAnnotation(HomeAssistantThreshold.class);


    }
}
