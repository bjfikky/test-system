package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.CreateTestRequest;
import com.benorim.testsystem.controller.api.request.LoginRequest;
import com.benorim.testsystem.controller.api.request.OptionRequest;
import com.benorim.testsystem.controller.api.request.QuestionRequest;
import com.benorim.testsystem.controller.api.request.TestAnswerRequest;
import com.benorim.testsystem.controller.api.response.ErrorResponse;
import com.benorim.testsystem.controller.api.response.JwtResponse;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.controller.api.response.SubmitTestResponse;
import com.benorim.testsystem.controller.api.response.TestResponse;
import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.enums.ErrorState;
import com.benorim.testsystem.enums.Role;
import com.benorim.testsystem.service.TestService;
import com.benorim.testsystem.service.UserService;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
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

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private String testTakerLoginToken;

    private String adminLoginToken;

    @Test
    @DisplayName("Test Taking APIs")
    void createTestQuestionsToSubmittingAnswers() {
        setTestTakerJwtToken();
        setAdminJwtToken();

        HttpHeaders adminRequestHeaders = new HttpHeaders();
        adminRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminRequestHeaders.set("Authorization", "Bearer " + adminLoginToken);

        // Create Questions
        List<QuestionRequest> questionRequests = getQuestionRequests();
        ParameterizedTypeReference<List<QuestionResponse>> postAllQuestionResponseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<QuestionResponse>> postAllQuestionResponse = restTemplate.exchange("/api/v1/admin/questions/addAll", HttpMethod.POST, new HttpEntity<>(questionRequests, adminRequestHeaders), postAllQuestionResponseType);
        assertEquals(HttpStatus.CREATED, postAllQuestionResponse.getStatusCode());

        User user = userService.findByEmail("testTaker@email.com");

        // Create Test Questions for Test Taker
        HttpHeaders testTakerRequestHeaders = new HttpHeaders();
        testTakerRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
        testTakerRequestHeaders.set("Authorization", "Bearer " + testTakerLoginToken);

        CreateTestRequest createTestRequest = new CreateTestRequest(user.getId(), 4);
        HttpEntity<CreateTestRequest> createTestRequestEntity = new HttpEntity<>(createTestRequest, testTakerRequestHeaders);
        ResponseEntity<TestResponse> postCreateTestResponse = restTemplate.postForEntity("/api/v1/test", createTestRequestEntity, TestResponse.class);
        assertEquals(HttpStatus.CREATED, postCreateTestResponse.getStatusCode());
        assertNotNull(postCreateTestResponse.getBody());
        assertNull(postCreateTestResponse.getBody().percentScore());
        assertEquals(4, postCreateTestResponse.getBody().numberOfQuestions());
        assertFalse(postCreateTestResponse.getBody().completed());

        Long testId = postCreateTestResponse.getBody().id();
        Long testTakerId = postCreateTestResponse.getBody().testTakerId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            /*
            Doing this in a transaction ensures that Test.GetQuestions are lazy loaded.
            Initially added @Transactional to the entire test method, but this meant
            that API calls where failing because the added objects are completely added
            yet before trying to GET them.
             */
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Option firstOption1;
                Option secondOption1;
                Option thirdOption1;
                Option fourthOption2;

                try {

                    com.benorim.testsystem.entity.Test test =
                            testService.getTestByIdAndTestTakerId(testId, testTakerId);

                    // We know we created 4 questions for this user, test the options we want to submit
                    firstOption1 = test.getQuestions().get(0).getOptions().get(0);
                    assertEquals("option 1", firstOption1.getText());
                    assertTrue(firstOption1.isCorrect());

                    secondOption1 = test.getQuestions().get(1).getOptions().get(0);
                    assertEquals("option 1", secondOption1.getText());
                    assertTrue(secondOption1.isCorrect());

                    thirdOption1 = test.getQuestions().get(2).getOptions().get(0);
                    assertEquals("option 1", thirdOption1.getText());
                    assertTrue(thirdOption1.isCorrect());

                    fourthOption2 = test.getQuestions().get(3).getOptions().get(1);
                    assertEquals("option 2", fourthOption2.getText());
                    assertFalse(fourthOption2.isCorrect());

                } catch (Exception e) {
                    status.setRollbackOnly(); // Rollback the transaction if an exception occurs
                    throw new RuntimeException("Error in yourMethod", e);
                }

                // Submitting an optionId that does not belong to the Test questions options
                List<Long> selectedOptionsIdsWithOneNotBelongOption = List.of(firstOption1.getId(), secondOption1.getId(), thirdOption1.getId(), 1000L);
                TestAnswerRequest testAnswerRequestWithOneNotBelongOption = new TestAnswerRequest(testId, testTakerId, selectedOptionsIdsWithOneNotBelongOption);
                HttpEntity<TestAnswerRequest> testAnswerRequestWithOneNotBelongOptionRequestEntity = new HttpEntity<>(testAnswerRequestWithOneNotBelongOption, testTakerRequestHeaders);
                ResponseEntity<ErrorResponse> postSubmitTestResponseWithOneNotBelongOption = restTemplate.postForEntity("/api/v1/test/submitTestAnswers", testAnswerRequestWithOneNotBelongOptionRequestEntity, ErrorResponse.class);
                assertEquals(HttpStatus.BAD_REQUEST, postSubmitTestResponseWithOneNotBelongOption.getStatusCode());
                assertNotNull(postSubmitTestResponseWithOneNotBelongOption.getBody());
                assertEquals(ErrorState.INVALID_OPTIONS, postSubmitTestResponseWithOneNotBelongOption.getBody().errorState());


                // Test Submit Answers using the above options
                List<Long> selectedOptionsIds = List.of(firstOption1.getId(), secondOption1.getId(), thirdOption1.getId(), fourthOption2.getId());
                TestAnswerRequest testAnswerRequest = new TestAnswerRequest(testId, testTakerId, selectedOptionsIds);
                HttpEntity<TestAnswerRequest> testAnswerRequestRequestEntity = new HttpEntity<>(testAnswerRequest, testTakerRequestHeaders);
                ResponseEntity<SubmitTestResponse> postSubmitTestResponse = restTemplate.postForEntity("/api/v1/test/submitTestAnswers", testAnswerRequestRequestEntity, SubmitTestResponse.class);
                assertEquals(HttpStatus.OK, postSubmitTestResponse.getStatusCode());
                assertNotNull(postSubmitTestResponse.getBody());
                assertTrue(postSubmitTestResponse.getBody().completed());
                assertEquals(75.0, postSubmitTestResponse.getBody().percentScore());
            }
        });
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

    private void setTestTakerJwtToken() {
        final String testTakerFirstName = "testTaker";
        final String testTakerLastName = "testTaker";
        final String testTakerEmail = "testTaker@email.com";
        final String testTakerPassword = "testtaker1234";
        final String testTakerEncryptedPassword = passwordEncoder.encode(testTakerPassword);

        try {
            userService.findByEmail(testTakerEmail);
        } catch (UsernameNotFoundException e) {
            User user = new User(testTakerLastName, testTakerFirstName, testTakerEmail, testTakerEncryptedPassword, Role.USER);
            User user1 = userService.createUser(user);
            System.out.println(user1.getPassword());
        }

        LoginRequest loginRequest = new LoginRequest(testTakerEmail, testTakerPassword);

        ResponseEntity<JwtResponse> postJwtResponse = restTemplate.postForEntity("/api/v1/auth/login", loginRequest, JwtResponse.class);
        assertEquals(HttpStatus.OK, postJwtResponse.getStatusCode());
        assertNotNull(postJwtResponse.getBody());
        testTakerLoginToken = postJwtResponse.getBody().token();
    }

    private void setAdminJwtToken() {
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
        adminLoginToken = postJwtResponse.getBody().token();
    }
}