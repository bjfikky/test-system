package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.CreateTestRequest;
import com.benorim.testsystem.controller.api.request.OptionRequest;
import com.benorim.testsystem.controller.api.request.QuestionRequest;
import com.benorim.testsystem.controller.api.request.TestAnswerRequest;
import com.benorim.testsystem.controller.api.request.TestTakerRequest;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.controller.api.response.SubmitTestResponse;
import com.benorim.testsystem.controller.api.response.TestResponse;
import com.benorim.testsystem.controller.api.response.TestTakerResponse;
import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.service.TestService;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Test API Tests")
class TestApiTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestService testService;

    @Test
    @Transactional
    @DisplayName("Test Taking APIs")
    void  createTestTakerToSubmittingAnswers() {

        // Create Questions
        List<QuestionRequest> questionRequests = getQuestionRequests();
        ParameterizedTypeReference<List<QuestionResponse>> postAllQuestionResponseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<QuestionResponse>> postAllQuestionResponse = restTemplate.exchange("/api/v1/questions/addAll", HttpMethod.POST, new HttpEntity<>(questionRequests), postAllQuestionResponseType);
        assertEquals(postAllQuestionResponse.getStatusCode(), HttpStatus.CREATED);

        // Create Test Taker
        TestTakerRequest testTakerRequest = new TestTakerRequest("testUsername");
        ResponseEntity<TestTakerResponse> postTestTakerResponse = restTemplate.postForEntity("/api/v1/testTaker", testTakerRequest, TestTakerResponse.class);
        assertEquals(postTestTakerResponse.getStatusCode(), HttpStatus.CREATED);
        assertNotNull(postTestTakerResponse.getBody());
        assertEquals("testUsername", postTestTakerResponse.getBody().username());

        // Create Test Questions for Test Taker
        CreateTestRequest createTestRequest = new CreateTestRequest(postTestTakerResponse.getBody().id(), 4);
        ResponseEntity<TestResponse> postCreateTestResponse = restTemplate.postForEntity("/api/v1/test", createTestRequest, TestResponse.class);
        assertEquals(postCreateTestResponse.getStatusCode(), HttpStatus.CREATED);
        assertNotNull(postCreateTestResponse.getBody());
        assertNull(postCreateTestResponse.getBody().percentScore());
        assertEquals(4, postCreateTestResponse.getBody().numberOfQuestions());
        assertFalse(postCreateTestResponse.getBody().completed());

        Long testId = postCreateTestResponse.getBody().id();
        Long testTakerId = postCreateTestResponse.getBody().testTakerId();

        com.benorim.testsystem.entity.Test test =
                testService.getTestByIdAndTestTakerId(testId, testTakerId);

        // We know we created 4 questions for this user, test the options we want to submit
        Option firstOption1 = test.getQuestions().get(0).getOptions().get(0);
        assertEquals("option 1", firstOption1.getText());
        assertTrue(firstOption1.isCorrect());

        Option secondOption1 = test.getQuestions().get(1).getOptions().get(0);
        assertEquals("option 1", secondOption1.getText());
        assertTrue(secondOption1.isCorrect());

        Option thirdOption1 = test.getQuestions().get(2).getOptions().get(0);
        assertEquals("option 1", thirdOption1.getText());
        assertTrue(thirdOption1.isCorrect());

        Option fourthOption2 = test.getQuestions().get(3).getOptions().get(1);
        assertEquals("option 2", fourthOption2.getText());
        assertFalse(fourthOption2.isCorrect());

        // Test Submit Answers using the above options
        List<Long> selectedOptionsIds = List.of(firstOption1.getId(), secondOption1.getId(), thirdOption1.getId(), fourthOption2.getId());
        TestAnswerRequest testAnswerRequest = new TestAnswerRequest(testId, testTakerId, selectedOptionsIds);
        ResponseEntity<SubmitTestResponse> postSubmitTestResponse = restTemplate.postForEntity("/api/v1/test/submitTestAnswers", testAnswerRequest, SubmitTestResponse.class);
        assertEquals(postSubmitTestResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(postSubmitTestResponse.getBody());
        assertTrue(postSubmitTestResponse.getBody().completed());
        assertEquals(75.0, postSubmitTestResponse.getBody().percentScore());


    }

    @NotNull
    private static List<QuestionRequest> getQuestionRequests() {
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

        QuestionRequest questionRequest3 = new QuestionRequest(
                "Test question 3",
                List.of(
                        new OptionRequest("option 1", true),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                ));

        QuestionRequest questionRequest4 = new QuestionRequest(
                "Test question 4",
                List.of(
                        new OptionRequest("option 1", true),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                ));

        QuestionRequest questionRequest5 = new QuestionRequest(
                "Test question 5",
                List.of(
                        new OptionRequest("option 1", true),
                        new OptionRequest("option 2", false),
                        new OptionRequest("option 3", false),
                        new OptionRequest("option 4", false)
                ));

        return List.of(questionRequest1, questionRequest2, questionRequest3, questionRequest4, questionRequest5);
    }
}