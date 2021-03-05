package org.igor.homeassistant.api;

import org.igor.homeassistant.api.response.HomeAssistantResponse;
import org.igor.homeassistant.dataaccess.model.Temperature;
import org.igor.homeassistant.exception.DataAccessException;
import org.igor.homeassistant.service.temperature.TemperatureService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API Controller for the Temperature collection
 */
@RestController
public class TemperatureController {

    private TemperatureService temperatureService;

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping(value = "/temperature", produces = {MediaType.APPLICATION_JSON_VALUE})
    public HomeAssistantResponse<List<Temperature>> getTemperature(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "group", required = false) String group) throws DataAccessException {
        HomeAssistantResponse<List<Temperature>> response = new HomeAssistantResponse();
        Temperature search = new Temperature();
        search.setName(name);
        search.setGroup(group);
        List<Temperature> temperatures = temperatureService.searchTemperatures(search);
        response.setData(temperatures);
        return response;
    }

    @GetMapping(value = "/temperature/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public HomeAssistantResponse<Temperature> getTemperaturebyId(@PathVariable("id") String id)
            throws DataAccessException {
        HomeAssistantResponse<Temperature> response = new HomeAssistantResponse();
        Temperature read = new Temperature(id);
        Temperature temperature = temperatureService.readTemperatures(read);
        response.setData(temperature);
        return response;
    }

    @PostMapping(value = "/temperature", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public HomeAssistantResponse<Temperature> postTemperature(@RequestBody Temperature temperature)
            throws DataAccessException {
        Temperature savedTemp = temperatureService.createTemperature(temperature);
        HomeAssistantResponse<Temperature> response = new HomeAssistantResponse<>();
        response.setData(savedTemp);
        return response;
    }

    @PutMapping(value = "/temperature/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public HomeAssistantResponse<Temperature> putTemperature(@PathVariable("id") String id,
                                                             @RequestBody Temperature temperature)
            throws DataAccessException {
        temperature.setId(id);
        Temperature savedTemp = temperatureService.updateTemperature(temperature);
        HomeAssistantResponse<Temperature> response = new HomeAssistantResponse<>();
        response.setData(savedTemp);
        return response;
    }

    @DeleteMapping(value = "/temperature/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public HomeAssistantResponse<List<Temperature>> deleteTemperature(@PathVariable("id") String id)
            throws DataAccessException {
        Temperature temperature = new Temperature(id);
        List<Temperature> removedTemps = temperatureService.removeTemperature(temperature);
        HomeAssistantResponse<List<Temperature>> response = new HomeAssistantResponse<>();
        response.setData(removedTemps);
        return response;
    }
}
