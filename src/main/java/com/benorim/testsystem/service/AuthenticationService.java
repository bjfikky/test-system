package com.benorim.testsystem.service;

import com.benorim.testsystem.controller.api.request.LoginRequest;
import com.benorim.testsystem.controller.api.request.RegisterRequest;
import com.benorim.testsystem.controller.api.response.JwtResponse;
import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.enums.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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

    public JwtResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        User user = userService.findByEmail(loginRequest.email());
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        return new JwtResponse(user.getEmail(), token, refreshToken);
    }
}
