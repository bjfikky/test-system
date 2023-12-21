package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.OptionRequest;
import com.benorim.testsystem.controller.api.request.QuestionRequest;
import com.benorim.testsystem.controller.api.response.ErrorResponse;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.enums.ErrorState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void addQuestionAndGetQuestion() {
        QuestionRequest questionRequest = new QuestionRequest(
                "Test question",
                List.of(
                        new OptionRequest("option 1", true),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                        ));

        // Test Add Question
        ResponseEntity<?> postQuestionResponse = restTemplate.postForEntity("/api/v1/questions", questionRequest, QuestionResponse.class);
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
        ResponseEntity<List<QuestionResponse>> postAllQuestionResponse = restTemplate.exchange("/api/v1/questions/addAll", HttpMethod.POST, new HttpEntity<>(questionRequests), postAllQuestionResponseType);
        assertEquals(postAllQuestionResponse.getStatusCode(), HttpStatus.CREATED);

        // Test Get All Questions
        ParameterizedTypeReference<List<QuestionResponse>> getAllQuestionsResponseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<QuestionResponse>> getAllQuestionsResponse = restTemplate.exchange("/api/v1/questions", HttpMethod.GET, null, getAllQuestionsResponseType);
        assertEquals(getAllQuestionsResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(getAllQuestionsResponse.getBody());
        assertFalse(getAllQuestionsResponse.getBody().isEmpty());

        // At this point, a total of three questions should have been inserted
        assertEquals(3, getAllQuestionsResponse.getBody().size());
        assertNotNull(getAllQuestionsResponse.getBody().get(0));

        // Test Get Question
        Long questionId = getAllQuestionsResponse.getBody().get(0).id();
        ResponseEntity<QuestionResponse> getOneQuestionResponse = restTemplate.getForEntity("/api/v1/questions/" + questionId, QuestionResponse.class);
        assertEquals(getOneQuestionResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(getOneQuestionResponse.getBody());
        assertEquals("Test question", getOneQuestionResponse.getBody().question());
        assertEquals(4, getOneQuestionResponse.getBody().options().size());

        // Test Delete One
        restTemplate.delete("/api/v1/questions/" + questionId);
        ResponseEntity<QuestionResponse> getDeletedQuestionResponse = restTemplate.getForEntity("/api/v1/questions/" + questionId, QuestionResponse.class);
        assertNull(getDeletedQuestionResponse.getBody());
        assertEquals(getDeletedQuestionResponse.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void addQuestionNoCorrectOption() {
        QuestionRequest questionRequest = new QuestionRequest(
                "Test question",
                List.of(
                        new OptionRequest("option 1", false),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                ));
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/v1/questions", questionRequest, ErrorResponse.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(response.getBody()).errorState(), ErrorState.INVALID_OPTIONS);
    }
}