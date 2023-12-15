package com.benorim.testsystem.controller.api.response;

import com.benorim.testsystem.enums.ErrorState;

public record ErrorResponse(String error, ErrorState errorState) {
}
