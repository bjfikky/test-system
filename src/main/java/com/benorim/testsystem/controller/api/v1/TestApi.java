package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.CreateTestRequest;
import com.benorim.testsystem.controller.api.request.GetTestRequest;
import com.benorim.testsystem.controller.api.request.TestTakerRequest;
import com.benorim.testsystem.controller.api.response.TestQuestionResponse;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.entity.Test;
import com.benorim.testsystem.entity.TestTaker;
import com.benorim.testsystem.mapper.QuestionMapper;
import com.benorim.testsystem.service.QuestionService;
import com.benorim.testsystem.service.TestService;
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

@RestController
@RequestMapping("/api/v1/test")
public class TestApi {

    private final QuestionService questionService;
    private final TestService testService;

    public TestApi(QuestionService questionService, TestService testService) {
        this.questionService = questionService;
        this.testService = testService;
    }

    @PostMapping
    public ResponseEntity<Void> createTestQuestions(@Valid @RequestBody CreateTestRequest createTestRequest) {
        TestTaker testTaker = testService.getTestTaker(createTestRequest.testTakerId());
        List<Question> questions = questionService.getRandomQuestions(createTestRequest.numberOfQuestions());

        Test test = testService.createTest(new Test(questions, testTaker));

        String uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(test.getId())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, uri);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PostMapping("/testTaker")
    public ResponseEntity<Void> createTestTaker(@Valid @RequestBody TestTakerRequest testTakerRequest) {
        TestTaker testTaker = testService.createTestTaker(testTakerRequest.username());

        String uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(testTaker.getId())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, uri);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PostMapping("/getTestQuestions")
    public ResponseEntity<List<TestQuestionResponse>> getTestQuestions(@Valid @RequestBody GetTestRequest getTestRequest) {
        Test test = testService.getTestByIdAndTestTakerId(getTestRequest.testId(), getTestRequest.testTakerId());
        return new ResponseEntity<>(QuestionMapper.mapQuestionsToQuestionsResponseForTest(test.getQuestions()), HttpStatus.OK);
    }


}
