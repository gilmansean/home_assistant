package org.igor.homeassistant.integration;

import com.google.gson.Gson;
import org.igor.homeassistant.api.request.AuthenticationRequest;
import org.igor.homeassistant.api.response.AuthenticationResponse;
import org.igor.homeassistant.api.response.HomeAssistantResponse;
import org.igor.homeassistant.dataaccess.model.Temperature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.File;
import java.util.Collections;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TemperatureHTTPTest {
    private static Logger log = Logger.getLogger(TemperatureHTTPTest.class.getName());

    static boolean needToCleanup = false;

    public static final String TEMP_DIR = "/tmp/integration_test/temperature";

    static {
        if(System.getProperty("DATA_ACCESS_FILEDIRECTORY") == null) {
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
    public void testUnauthenticatedCall() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/api/v1/temperature",
                                                  String.class)).contains("Unauthorized");
    }

    @Test
    public void testTempatureAPIs() throws Exception {
        /*
        Get the auth token and set it on the restTemplate so all requests are sent with it.
         */
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("igoruser", "password");
        AuthenticationResponse authenticationResponse = this.restTemplate.postForObject(
                "http://localhost:" + port + "/authenticate", authenticationRequest, AuthenticationResponse.class);
        restTemplate.getRestTemplate()
                    .setInterceptors(Collections.singletonList((request, body, execution) -> {
                        request.getHeaders()
                               .setBearerAuth(authenticationResponse.getToken());
                        return execution.execute(request, body);
                    }));

        Temperature newTemp = new Temperature();
        newTemp.setName("testingTemp");
        newTemp.setGroup("testing");
        newTemp.setReading(10.0F);
        /*
        Testing the POST.
         */
        Temperature created = pullTemperatureFromResponse(
                this.restTemplate.postForObject("http://localhost:" + port + "/api/v1/temperature", newTemp,
                                                String.class));
        assertNotNull(created);
        assertNotNull(created.getId());
        assertNotEquals("", created.getId());
        created.setName("updated");
        /*
        Testing the PUT
         */
        this.restTemplate.put("http://localhost:" + port + "/api/v1/temperature/" + created.getId(), created);
        /*
        Testing the GET on and ID and verifying the PUT
         */
        Temperature updated = pullTemperatureFromResponse(
                this.restTemplate.getForObject("http://localhost:" + port + "/api/v1/temperature/" + created.getId(),
                                               String.class));
        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals("updated", updated.getName());
        /*
        Testing the GET
         */
        Temperature[] temperatures = pullTemperaturesFromResponse(
                this.restTemplate.getForObject("http://localhost:" + port + "/api/v1/temperature", String.class));
        assertNotNull(temperatures);
        assertEquals(1, temperatures.length);
        /*
        Testing the DELETE
         */
        this.restTemplate.delete("http://localhost:" + port + "/api/v1/temperature/" + created.getId());
        Temperature deleted = pullTemperatureFromResponse(
                this.restTemplate.getForObject("http://localhost:" + port + "/api/v1/temperature/" + created.getId(),
                                               String.class));
        assertNull(deleted);
    }

    private Temperature pullTemperatureFromResponse(String response) {
        Temperature responseTemp = gson.fromJson(gson.toJson(gson.fromJson(response, HomeAssistantResponse.class)
                                                                 .getData()), Temperature.class);
        return responseTemp;
    }

    private Temperature[] pullTemperaturesFromResponse(String response) {
        Temperature[] responseTemps = gson.fromJson(gson.toJson(gson.fromJson(response, HomeAssistantResponse.class)
                                                                    .getData()), Temperature[].class);
        return responseTemps;
    }

    @AfterAll
    static void afterAll() {
        //clean up is we made our own temp location
        if(needToCleanup) {
            File testingDir = new File(TEMP_DIR);
            if(testingDir != null) {
                for(File file : testingDir.listFiles()) {
                    file.delete();
                }
                testingDir.delete();
            }
        }
    }
}
