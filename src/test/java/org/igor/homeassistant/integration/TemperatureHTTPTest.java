package org.igor.homeassistant.integration;

import com.google.gson.Gson;
import org.igor.homeassistant.api.response.HomeAssistantResponse;
import org.igor.homeassistant.dataaccess.model.Temperature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TemperatureHTTPTest {

    static boolean needToCleanup = false;

    public static final String TEMP_DIR = "/tmp/integration_test/temperature";

    static {
        if (System.getProperty("DATA_ACCESS_FILEDIRECTORY") == null) {
            System.setProperty("DATA_ACCESS_FILEDIRECTORY", TEMP_DIR);
            needToCleanup = true;
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final Gson gson = new Gson();

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        Temperature newTemp = new Temperature();
        newTemp.setName("testingTemp");
        newTemp.setGroup("testing");
        newTemp.setReading(10.0F);
        /*
        Testing the POST.
         */
        Temperature created = pullTmperatureFromResponse(
                this.restTemplate.postForObject("http://localhost:" + port + "/temperature", newTemp, String.class));
        assertNotNull(created);
        assertNotNull(created.getId());
        assertNotEquals("", created.getId());
        created.setName("updated");
        /*
        Testing the PUT
         */
        this.restTemplate.put("http://localhost:" + port + "/temperature/" + created.getId(), created);
        /*
        Testing the GET on and ID and verifying the PUT
         */
        Temperature updated = pullTmperatureFromResponse(
                this.restTemplate.getForObject("http://localhost:" + port + "/temperature/" + created.getId(),
                                               String.class));
        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("updated", updated.getName());
        /*
        Testing the GET
         */
        Temperature[] temperatures = pullTmperaturesFromResponse(
                this.restTemplate.getForObject("http://localhost:" + port + "/temperature", String.class));
        assertNotNull(temperatures);
        assertEquals(1, temperatures.length);
        /*
        Testing the DELETE
         */
        this.restTemplate.delete("http://localhost:" + port + "/temperature/" + created.getId());
        Temperature deleted = pullTmperatureFromResponse(
                this.restTemplate.getForObject("http://localhost:" + port + "/temperature/" + created.getId(),
                                               String.class));
        assertNull(deleted);
    }

    private Temperature pullTmperatureFromResponse(String response) {
        Temperature responseTemp = gson.fromJson(gson.toJson(gson.fromJson(response, HomeAssistantResponse.class)
                                                                 .getData()), Temperature.class);
        return responseTemp;
    }

    private Temperature[] pullTmperaturesFromResponse(String response) {
        Temperature[] responseTemps = gson.fromJson(gson.toJson(gson.fromJson(response, HomeAssistantResponse.class)
                                                                    .getData()), Temperature[].class);
        return responseTemps;
    }

    @AfterAll
    static void afterAll() {
        //clean up is we made our own temp location
        if (needToCleanup) {
            File testingDir = new File(TEMP_DIR);
            for (File file : testingDir.listFiles()) {
                file.delete();
            }
            testingDir.delete();
        }
    }
}
