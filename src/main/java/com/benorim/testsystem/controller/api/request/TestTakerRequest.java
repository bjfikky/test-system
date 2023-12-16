package com.benorim.testsystem.controller.api.request;

import jakarta.validation.constraints.NotBlank;

public record TestTakerRequest(@NotBlank String username) {
}
