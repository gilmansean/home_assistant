package org.igor.homeassistant.integration;

import org.igor.homeassistant.api.request.AuthenticationRequest;
import org.igor.homeassistant.api.response.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationHTTPTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetToken() throws Exception {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("igoruser", "password");
        AuthenticationResponse authenticationResponse = this.restTemplate.postForObject(
                "http://localhost:" + port + "/authenticate", authenticationRequest, AuthenticationResponse.class);

        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getToken());

    }

    @Test
    public void testBadCreds() throws Exception {
        assertThrows(RestClientException.class, () -> {
            AuthenticationRequest badAuthenticationRequest = new AuthenticationRequest("igoruser", "notcorrect");
            AuthenticationResponse badAuthenticationResponse = this.restTemplate.postForObject(
                    "http://localhost:" + port + "/authenticate", badAuthenticationRequest,
                    AuthenticationResponse.class);
        });
    }
}
