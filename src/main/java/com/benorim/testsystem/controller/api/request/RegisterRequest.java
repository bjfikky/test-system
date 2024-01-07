package com.benorim.testsystem.controller.api.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank String email, @NotBlank String password) {
}
