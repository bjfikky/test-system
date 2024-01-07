package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.LoginRequest;
import com.benorim.testsystem.controller.api.request.RegisterRequest;
import com.benorim.testsystem.controller.api.response.JwtResponse;
import com.benorim.testsystem.controller.api.response.RegisterResponse;
import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.enums.Role;
import com.benorim.testsystem.service.JwtService;
import com.benorim.testsystem.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Auth API Tests")
class AuthApiTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Test
    void registerAndLogin() {
        final String testFirstName = "firstName";
        final String testLastName = "lastName";
        final String testEmail = "test@email.com";
        final String testPassword = "password1234";

        // Test Register
        RegisterRequest registerRequest = new RegisterRequest(testFirstName, testLastName, testEmail, testPassword);
        ResponseEntity<RegisterResponse> postRegisterResponse = restTemplate.postForEntity("/api/v1/auth/register", registerRequest, RegisterResponse.class);
        assertEquals(HttpStatus.CREATED, postRegisterResponse.getStatusCode());
        assertNotNull(postRegisterResponse.getBody());
        assertEquals(testFirstName, postRegisterResponse.getBody().firstName());
        assertEquals(testLastName, postRegisterResponse.getBody().lastName());
        assertEquals(testEmail, postRegisterResponse.getBody().email());
        assertEquals(Role.USER, postRegisterResponse.getBody().role());

        // Test Login
        LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
        ResponseEntity<JwtResponse> postJwtResponse = restTemplate.postForEntity("/api/v1/auth/login", loginRequest, JwtResponse.class);
        assertEquals(HttpStatus.OK, postJwtResponse.getStatusCode());
        assertNotNull(postJwtResponse.getBody());
        assertEquals(testEmail, postJwtResponse.getBody().email());

        // Validate token
        User user = userService.findByEmail(testEmail);
        assertTrue(jwtService.isTokenValid(postJwtResponse.getBody().token(), user));
    }

}