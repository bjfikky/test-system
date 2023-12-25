package com.benorim.testsystem.controller.api.response;

public record SubmitTestResponse(Long testId, Long testTakerId, boolean completed ,Double percentScore) {
}
