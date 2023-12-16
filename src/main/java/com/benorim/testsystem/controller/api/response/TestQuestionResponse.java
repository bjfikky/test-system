package com.benorim.testsystem.controller.api.response;

import java.util.List;

public record TestQuestionResponse(Long id, String question, List<TestOptionResponse> options) {
}
