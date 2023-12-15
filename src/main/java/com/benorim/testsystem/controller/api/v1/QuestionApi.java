package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.QuestionRequest;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.mapper.QuestionMapper;
import com.benorim.testsystem.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionApi {

    private final QuestionService questionService;

    public QuestionApi(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<Void> addQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        List<Option> options = questionRequest.options().stream()
                .map(optionRequest -> new Option(optionRequest.text(), optionRequest.correct()))
                .collect(Collectors.toList());

        Question question = new Question(questionRequest.text(), options);
        question.getOptions().forEach(option -> option.setQuestion(question));

        Question addedQuestion = questionService.addQuestion(question);

        String uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedQuestion.getId())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, uri);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        QuestionResponse response = QuestionMapper.mapQuestionToQuestionResponse(question);
        return response != null ? new ResponseEntity<>(response, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
