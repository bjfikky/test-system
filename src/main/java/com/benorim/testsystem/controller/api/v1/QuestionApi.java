package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.QuestionRequest;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.benorim.testsystem.mapper.QuestionMapper.mapQuestionToQuestionResponse;
import static com.benorim.testsystem.mapper.QuestionMapper.mapQuestionsToQuestionsResponse;

@RestController
@RequestMapping("/api/v1/admin/questions")
public class QuestionApi {

    private final QuestionService questionService;

    public QuestionApi(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> addQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
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
        return new ResponseEntity<>(mapQuestionToQuestionResponse(addedQuestion), headers, HttpStatus.CREATED);
    }

    @PostMapping("/addAll")
    public ResponseEntity<List<QuestionResponse>> addQuestions(@Valid @RequestBody List<QuestionRequest> questionRequests) {
        List<Question> questions = new ArrayList<>();

        questionRequests.forEach(questionRequest -> {
            List<Option> options = questionRequest.options().stream()
                    .map(optionRequest -> new Option(optionRequest.text(), optionRequest.correct()))
                    .toList();

            Question question = new Question(questionRequest.text(), options);
            question.getOptions().forEach(option -> option.setQuestion(question));
            questions.add(question);
        });

        List<Question> addedQuestions = questionService.addQuestions(questions);
        return new ResponseEntity<>(mapQuestionsToQuestionsResponse(addedQuestions), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return new ResponseEntity<>(mapQuestionsToQuestionsResponse(questions), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        QuestionResponse response = mapQuestionToQuestionResponse(question);
        return response != null ? new ResponseEntity<>(response, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
