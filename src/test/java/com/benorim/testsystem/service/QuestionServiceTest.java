package com.benorim.testsystem.service;

import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.exception.InvalidOptionsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class QuestionServiceTest {

    @Test
    @DisplayName("Validate options, no exception")
    public void validateOptions() {
        List<Option> options = List.of(new Option("option 1", true),
                new Option("option 2", false),
                new Option("option 3", false),
                new Option("option 4", false));

        Assertions.assertDoesNotThrow(() -> QuestionService.validateOptions(options));
    }

    @Test
    @DisplayName("Validate options, throws exception")
    public void validateOptionsException() {
        List<Option> options = List.of(new Option("option 1", false),
                new Option("option 2", false),
                new Option("option 3", false),
                new Option("option 4", false));

        Assertions.assertThrows(InvalidOptionsException.class, () -> QuestionService.validateOptions(options));
    }
}