package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.TestTakerRequest;
import com.benorim.testsystem.controller.api.response.TestQuestionResponse;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.entity.TestTaker;
import com.benorim.testsystem.mapper.QuestionMapper;
import com.benorim.testsystem.service.QuestionService;
import com.benorim.testsystem.service.TestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<List<TestQuestionResponse>> getTestQuestions(@RequestParam int numberOfQuestions) {
        List<Question> questions = questionService.getRandomQuestions(numberOfQuestions);

        return new ResponseEntity<>(QuestionMapper.mapQuestionsToQuestionsResponseForTest(questions), HttpStatus.OK);
    }
}
