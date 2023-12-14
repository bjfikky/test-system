package com.benorim.testsystem.controller.api.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record QuestionRequest(@NotBlank String text, List<OptionRequest> options) {
}
