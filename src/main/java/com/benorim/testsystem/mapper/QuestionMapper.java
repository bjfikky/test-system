package com.benorim.testsystem.mapper;

import com.benorim.testsystem.controller.api.response.OptionResponse;
import com.benorim.testsystem.controller.api.response.QuestionResponse;
import com.benorim.testsystem.entity.Question;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {
    public static QuestionResponse mapQuestionToQuestionResponse(Question question) {
        if (question == null) return null;

        List<OptionResponse> optionResponseList = question.getOptions().stream()
                .map(option -> new OptionResponse(option.getText(), option.isCorrect()))
                .collect(Collectors.toList());

        return  new QuestionResponse(question.getText(), optionResponseList);
    }
}
