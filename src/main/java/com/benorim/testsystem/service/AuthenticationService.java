package com.benorim.testsystem.service;

import com.benorim.testsystem.controller.api.request.RegisterRequest;
import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest registerRequest) {
        User user = new User(
                registerRequest.firstName(),
                registerRequest.lastName(),
                registerRequest.email(),
                passwordEncoder.encode(registerRequest.password()),
                Role.USER
        );

        return userService.createUser(user);
    }
}
