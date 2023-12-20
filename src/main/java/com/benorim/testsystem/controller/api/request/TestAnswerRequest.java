package com.benorim.testsystem.controller.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TestAnswerRequest(@NotNull Long testId, @NotNull Long testTakerId, @NotEmpty List<Long> selectedOptionsIds) {
}
