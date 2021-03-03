package org.igor.homeassistant.temperature;

import org.igor.homeassistant.response.HomeAssistantResponse;
import org.igor.homeassistant.model.Temperature;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemperatureController {

    private SearchTemperatureService searchTemperatureService;

    public TemperatureController(SearchTemperatureService searchTemperatureService) {
        this.searchTemperatureService = searchTemperatureService;
    }

    @GetMapping(value = "/temperature",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public HomeAssistantResponse<Temperature[]> getTemperature(
            @RequestParam(value = "name", required = false) String name) {
        HomeAssistantResponse<Temperature[]> response = new HomeAssistantResponse();
        response.setData(searchTemperatureService.searchTemperatures(name));
        return response;
    }
}
