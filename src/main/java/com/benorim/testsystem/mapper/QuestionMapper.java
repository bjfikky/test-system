package com.benorim.testsystem.mapper;

import com.benorim.testsystem.controller.api.response.OptionResponse;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.controller.api.response.TestOptionResponse;
import com.benorim.testsystem.controller.api.response.TestQuestionResponse;
import com.benorim.testsystem.entity.Question;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {
    public static QuestionResponse mapQuestionToQuestionResponse(Question question) {
        if (question == null) return null;

        List<OptionResponse> optionResponseList = question.getOptions().stream()
                .map(option -> new OptionResponse(option.getId(), option.getText(), option.isCorrect()))
                .collect(Collectors.toList());

        return new QuestionResponse(question.getId(), question.getText(), optionResponseList);
    }

    public static TestQuestionResponse mapQuestionToQuestionResponseForTest(Question question) {
        if (question == null) return null;

        List<TestOptionResponse> optionResponseList = question.getOptions().stream()
                .map(option -> new TestOptionResponse(option.getId(), option.getText()))
                .collect(Collectors.toList());

        return new TestQuestionResponse(question.getId(), question.getText(), optionResponseList);
    }

    public static List<QuestionResponse> mapQuestionsToQuestionsResponse(List<Question> questions) {
        if (CollectionUtils.isEmpty(questions)) return null;

        List<QuestionResponse> questionResponses = new ArrayList<>();

        questions.forEach(question -> questionResponses.add(mapQuestionToQuestionResponse(question)));

        return questionResponses;
    }

    public static List<TestQuestionResponse> mapQuestionsToQuestionsResponseForTest(List<Question> questions) {
        if (CollectionUtils.isEmpty(questions)) return null;

        List<TestQuestionResponse> questionResponses = new ArrayList<>();

        questions.forEach(question -> questionResponses.add(mapQuestionToQuestionResponseForTest(question)));

        return questionResponses;
    }
}
