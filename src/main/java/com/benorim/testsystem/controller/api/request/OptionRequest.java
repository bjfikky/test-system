package com.benorim.testsystem.controller.api.request;

import jakarta.validation.constraints.NotBlank;

public record OptionRequest(@NotBlank String text, boolean correct) {
}
