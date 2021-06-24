package org.igor.homeassistant.service.temperature;

import org.igor.homeassistant.dataaccess.ObjectDataAccess;
import org.igor.homeassistant.dataaccess.model.Temperature;
import org.igor.homeassistant.exception.DataAccessException;
import org.igor.homeassistant.utility.UniqueId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "temperature")
public class TemperatureService {
    private ObjectDataAccess objectDataAccess;
    private TemperatureReportingService temperatureReportingService;

    public TemperatureService(ObjectDataAccess objectDataAccess,
                              TemperatureReportingService temperatureReportingService) {
        this.objectDataAccess = objectDataAccess;
        this.temperatureReportingService = temperatureReportingService;
    }

    public List<Temperature> searchTemperatures(Temperature search) throws DataAccessException {
        List<Temperature> temperatures = objectDataAccess.searchObjects(search);
        return temperatures;
    }

    public Temperature readTemperatures(Temperature read) throws DataAccessException {
        Temperature temperature = objectDataAccess.readObject(read);
        return temperature;
    }

    public Temperature createTemperature(Temperature temperature) throws DataAccessException {
        Temperature newTemp = UniqueId.createWithUID(Temperature.class);
        newTemp.setName(temperature.getName());
        newTemp.setGroup(temperature.getGroup());
        newTemp.setReading(temperature.getReading());
        if(temperature.getReadingDate() == null) {
            newTemp.setReadingDate(Calendar.getInstance()
                                           .getTime());
        } else {
            newTemp.setReadingDate(temperature.getReadingDate());
        }
        newTemp.setReadingScale(temperature.getReadingScale());
        objectDataAccess.saveObject(newTemp);
        temperatureReportingService.monitorThresholds(newTemp);
        return newTemp;
    }

    public Temperature updateTemperature(Temperature temperature) throws DataAccessException {
        Temperature existing = objectDataAccess.readObject(temperature);
        if(existing == null) {
            throw new DataAccessException("Can not update a temperature that does not exist");
        } else {
            objectDataAccess.saveObject(temperature);
            temperatureReportingService.monitorThresholds(temperature);
        }
        return temperature;
    }

    public List<Temperature> removeTemperature(Temperature temperature) throws DataAccessException {
        return objectDataAccess.removeObjects(temperature);
    }

}
