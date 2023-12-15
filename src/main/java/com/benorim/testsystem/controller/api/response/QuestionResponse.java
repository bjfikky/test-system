package com.benorim.testsystem.controller.api.response;

import java.util.List;

public record QuestionResponse(String question, List<OptionResponse> options) {
}
