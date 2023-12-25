package com.benorim.testsystem.controller.api.response;

public record TestResponse(Long id, boolean completed, Integer numberOfQuestions, Double percentScore, Long testTakerId) {

}
