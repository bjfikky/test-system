package com.benorim.testsystem.controller.api.response;

import java.util.List;

public record QuestionResponse(Long id, String question, List<OptionResponse> options) {
}
