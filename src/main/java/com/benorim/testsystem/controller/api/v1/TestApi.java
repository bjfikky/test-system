package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.CreateTestRequest;
import com.benorim.testsystem.controller.api.request.GetTestRequest;
import com.benorim.testsystem.controller.api.request.TestAnswerRequest;
import com.benorim.testsystem.controller.api.response.SubmitTestResponse;
import com.benorim.testsystem.controller.api.response.TestQuestionResponse;
import com.benorim.testsystem.controller.api.response.TestResponse;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.entity.Test;
import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.mapper.QuestionMapper;
import com.benorim.testsystem.service.QuestionService;
import com.benorim.testsystem.service.TestService;
import com.benorim.testsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

import static com.benorim.testsystem.mapper.TestMapper.mapTestToTestResponse;

@RestController
@RequestMapping("/api/v1/test")
public class TestApi {

    private final QuestionService questionService;
    private final TestService testService;
    private final UserService userService;

    public TestApi(QuestionService questionService, TestService testService, UserService userService) {
        this.questionService = questionService;
        this.testService = testService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TestResponse> createTestQuestions(@Valid @RequestBody CreateTestRequest createTestRequest) {
        User testTaker = userService.findById(createTestRequest.testTakerId());
        List<Question> questions = questionService.getRandomQuestions(createTestRequest.numberOfQuestions());

        Test test = testService.createTest(new Test(questions, testTaker));

        String uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(test.getId())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, uri);
        return new ResponseEntity<>(mapTestToTestResponse(test), headers, HttpStatus.CREATED);
    }

    @PostMapping("/getTestQuestions")
    public ResponseEntity<List<TestQuestionResponse>> getTestQuestions(@Valid @RequestBody GetTestRequest getTestRequest) {
        Test test = testService.getTestByIdAndTestTakerId(getTestRequest.testId(), getTestRequest.testTakerId());
        return new ResponseEntity<>(QuestionMapper.mapQuestionsToQuestionsResponseForTest(test.getQuestions()), HttpStatus.OK);
    }

    @PostMapping("/submitTestAnswers")
    public ResponseEntity<SubmitTestResponse> submitTestAnswers(@Valid @RequestBody TestAnswerRequest testAnswerRequest) {
        Test test = testService.submitTestAnswers(
                testAnswerRequest.testId(),
                testAnswerRequest.testTakerId(),
                testAnswerRequest.selectedOptionsIds());

        SubmitTestResponse submitTestResponse = new SubmitTestResponse(testAnswerRequest.testId(), testAnswerRequest.testTakerId(), test.isCompleted(), test.getPercentScore());
        return  new ResponseEntity<>(submitTestResponse, HttpStatus.OK);
    }
}
