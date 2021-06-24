package org.igor.homeassistant.service.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenUtilTest {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    User user = new User("igoruser", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
                         new java.util.ArrayList<>());
    User user2 = new User("user", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
                          new java.util.ArrayList<>());

    @Test
    void getUsernameFromToken() {
        String token = jwtTokenUtil.generateToken(user);
        assertEquals(user.getUsername(), jwtTokenUtil.getUsernameFromToken(token));
    }

    @Test
    void getExpirationDateFromToken() {
        Date expectedExpire = new Date(System.currentTimeMillis() + jwtTokenUtil.getTokenExpireInSeconds() * 1000);
        String token = jwtTokenUtil.generateToken(user);
        Date tokenExpire = jwtTokenUtil.getExpirationDateFromToken(token);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        assertEquals(simpleDateFormat.format(expectedExpire), simpleDateFormat.format(tokenExpire));
    }

    @Test
    void getClaimFromToken() {
        //TODO finish this one out once we get the claims stuff saved to a data store.
    }

    @Test
    void generateToken() {
        String token = jwtTokenUtil.generateToken(user);
        assertNotNull(token);
        System.out.println(token);
        assertTrue(jwtTokenUtil.validateToken(token, user));
    }

    @Test
    void validateToken() {
        String token = jwtTokenUtil.generateToken(user);
        assertNotNull(token);
        assertTrue(jwtTokenUtil.validateToken(token, user));
        //wrong user
        assertFalse(jwtTokenUtil.validateToken(token, user2));
        //old token
        assertFalse(jwtTokenUtil.validateToken(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpZ29ydXNlciIsImlhdCI6MTYyNDU2MjkwOCwiZXhwIjoxNjI1NDI2OTA4fQ.tqG4YF1zoi4G5eZCNskMDaiHA0tp2gjON6MWr32VYRwdP7pFrKOphb1DntsI87rRVmylYBRu9ikKup4pcJWc2w",
                user));
    }
}