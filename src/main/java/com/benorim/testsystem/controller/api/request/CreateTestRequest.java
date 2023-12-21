package com.benorim.testsystem.controller.api.request;

public record CreateTestRequest(Long testTakerId, int numberOfQuestions) {
}
