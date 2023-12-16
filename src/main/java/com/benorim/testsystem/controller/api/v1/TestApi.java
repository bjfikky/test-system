package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.response.TestQuestionResponse;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.mapper.QuestionMapper;
import com.benorim.testsystem.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
public class TestApi {

    private final QuestionService questionService;

    public TestApi(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<TestQuestionResponse>> getTestQuestions(@RequestParam int numberOfQuestions) {
        List<Question> questions = questionService.getRandomQuestions(numberOfQuestions);

        return new ResponseEntity<>(QuestionMapper.mapQuestionsToQuestionsResponseForTest(questions), HttpStatus.OK);
    }
}
