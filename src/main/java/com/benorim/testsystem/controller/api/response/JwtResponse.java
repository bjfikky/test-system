package com.benorim.testsystem.controller.api.response;

public record JwtResponse(String email, String token, String refreshToken) {
}
