package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.LoginRequest;
import com.benorim.testsystem.controller.api.request.OptionRequest;
import com.benorim.testsystem.controller.api.request.QuestionRequest;
import com.benorim.testsystem.controller.api.response.ErrorResponse;
import com.benorim.testsystem.controller.api.response.JwtResponse;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.enums.ErrorState;
import com.benorim.testsystem.enums.Role;
import com.benorim.testsystem.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Question API Tests")
class QuestionApiTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    private final TestRestTemplate restTemplate;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private String loginToken;

    @Autowired
    public QuestionApiTest(TestRestTemplate restTemplate, UserService userService, PasswordEncoder passwordEncoder) {
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    void addQuestionAndGetQuestion() {
        setJwtToken();

        QuestionRequest questionRequest = new QuestionRequest(
                "Test question",
                List.of(
                        new OptionRequest("option 1", true),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                        ));

        // Test Add Question
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + loginToken);

        HttpEntity<QuestionRequest> questionRequestEntity = new HttpEntity<>(questionRequest, headers);
        ResponseEntity<?> postQuestionResponse = restTemplate.postForEntity("/api/v1/admin/questions", questionRequestEntity, JwtResponse.class);
        assertEquals(postQuestionResponse.getStatusCode(), HttpStatus.CREATED);

        // Test Add Questions
        QuestionRequest questionRequest1 = new QuestionRequest(
                "Test question 1",
                List.of(
                        new OptionRequest("option 1", true),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                ));

        QuestionRequest questionRequest2 = new QuestionRequest(
                "Test question 2",
                List.of(
                        new OptionRequest("option 1", true),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                ));

        List<QuestionRequest> questionRequests = List.of(questionRequest1, questionRequest2);

        ParameterizedTypeReference<List<QuestionResponse>> postAllQuestionResponseType = new ParameterizedTypeReference<>() {};
        HttpEntity<List<QuestionRequest>> questionsRequestEntity = new HttpEntity<>(questionRequests, headers);
        ResponseEntity<List<QuestionResponse>> postAllQuestionResponse = restTemplate.exchange("/api/v1/admin/questions/addAll", HttpMethod.POST, questionsRequestEntity, postAllQuestionResponseType);
        assertEquals( HttpStatus.CREATED, postAllQuestionResponse.getStatusCode());

        // Test Get All Questions
        ParameterizedTypeReference<List<QuestionResponse>> getAllQuestionsResponseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<QuestionResponse>> getAllQuestionsResponse = restTemplate.exchange("/api/v1/admin/questions", HttpMethod.GET, new HttpEntity<>(null, headers), getAllQuestionsResponseType);
        assertEquals( HttpStatus.OK, getAllQuestionsResponse.getStatusCode());
        assertNotNull(getAllQuestionsResponse.getBody());
        assertFalse(getAllQuestionsResponse.getBody().isEmpty());

        // At this point, a total of three questions should have been inserted
        assertEquals(3, getAllQuestionsResponse.getBody().size());
        assertNotNull(getAllQuestionsResponse.getBody().get(0));

        // Test Get Question
        Long questionId = getAllQuestionsResponse.getBody().get(0).id();
        ResponseEntity<QuestionResponse> getOneQuestionResponse = restTemplate.exchange("/api/v1/admin/questions/" + questionId, HttpMethod.GET, new HttpEntity<>(null, headers), QuestionResponse.class);
        assertEquals(HttpStatus.OK, getOneQuestionResponse.getStatusCode());
        assertNotNull(getOneQuestionResponse.getBody());
        assertEquals("Test question", getOneQuestionResponse.getBody().question());
        assertEquals(4, getOneQuestionResponse.getBody().options().size());

        // Test Delete One
        restTemplate.exchange("/api/v1/admin/questions/" + questionId, HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);
        ResponseEntity<QuestionResponse> getDeletedQuestionResponse = restTemplate.exchange("/api/v1/admin/questions/" + questionId, HttpMethod.GET, new HttpEntity<>(null, headers), QuestionResponse.class);
        assertNull(getDeletedQuestionResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND, getDeletedQuestionResponse.getStatusCode());
    }

    @Test
    void addQuestionNoCorrectOption() {
        setJwtToken();

        QuestionRequest questionRequest = new QuestionRequest(
                "Test question",
                List.of(
                        new OptionRequest("option 1", false),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                ));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + loginToken);
        HttpEntity<QuestionRequest> questionRequestEntity = new HttpEntity<>(questionRequest, headers);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/v1/admin/questions", questionRequestEntity, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Objects.requireNonNull(response.getBody()).errorState(), ErrorState.INVALID_OPTIONS);
    }

    private void setJwtToken() {
        final String adminLastName = "admin";
        final String adminFirstName = "admin";
        final String adminEmail = "admin@test.com";
        final String adminPassword = "admin1234";
        final String adminEncryptedPassword = this.passwordEncoder.encode(adminPassword);

        try {
            userService.findByEmail(adminEmail);
        } catch (UsernameNotFoundException e) {
            User user = new User(adminFirstName, adminLastName, adminEmail, adminEncryptedPassword, Role.ADMIN);
            this.userService.createUser(user);
        }

        LoginRequest loginRequest = new LoginRequest(adminEmail, adminPassword);

        ResponseEntity<JwtResponse> postJwtResponse = this.restTemplate.postForEntity("/api/v1/auth/login", loginRequest, JwtResponse.class);
        assertEquals(HttpStatus.OK, postJwtResponse.getStatusCode());
        assertNotNull(postJwtResponse.getBody());
        loginToken = postJwtResponse.getBody().token();
    }
}