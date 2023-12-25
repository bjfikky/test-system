package com.benorim.testsystem.mapper;

import com.benorim.testsystem.controller.api.response.TestResponse;
import com.benorim.testsystem.entity.Test;

public class TestMapper {
    public static TestResponse mapTestToTestResponse(Test test) {
        if (test == null) return null;

        return new TestResponse(test.getId(), test.isCompleted(), test.getQuestions().size(), test.getPercentScore(), test.getTestTaker().getId());
    }
}
