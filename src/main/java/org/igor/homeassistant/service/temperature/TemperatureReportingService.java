package org.igor.homeassistant.service.temperature;

import org.igor.homeassistant.dataaccess.model.Temperature;
import org.igor.homeassistant.service.threshold.ThresholdService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * NOTES-----------------------
 *
 * This will send a monitor check to see if the reading is outside limits
 *
 * This will also have a scheduled service on it to run a report on temps
 *
 * ----------------------------
 */
@Component
public class TemperatureReportingService {

    private ThresholdService thresholdService;

    public TemperatureReportingService(ThresholdService thresholdService) {
        this.thresholdService = thresholdService;
    }

    @Async
    public void monitorThresholds(Temperature temperature) {
        thresholdService.monitorThreshold(temperature);
    }
}
