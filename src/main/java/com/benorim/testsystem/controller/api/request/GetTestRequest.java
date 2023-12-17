package com.benorim.testsystem.controller.api.request;

import jakarta.validation.constraints.NotNull;

public record GetTestRequest(@NotNull Long testId, @NotNull Long testTakerId) {
}
