package org.igor.homeassistant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
Smoke test of the application.  Just make sure we can at least start up.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeAssistantApplicationTests {
    @Autowired
    private HomeAssistantApplication homeAssistantApplication;

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(homeAssistantApplication);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;
    @Value("${application-description}")
    private String appDesciption;

    @Test
    public void checkForSwaggerStuff() throws Exception {
        //make sure the spec URL is up
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + apiDocsPath, String.class)).contains(
                appDesciption);
        //make sure the swagger UI is running
        assertThat(
                this.restTemplate.getForObject("http://localhost:" + port + "/swagger-ui.html", String.class)).contains(
                "<title>Swagger UI</title>");
    }
}
