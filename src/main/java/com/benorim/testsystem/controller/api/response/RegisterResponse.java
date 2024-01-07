package com.benorim.testsystem.controller.api.response;

import com.benorim.testsystem.enums.Role;

public record RegisterResponse(Long id, String firstName, String lastName, String email, Role role) {
}
