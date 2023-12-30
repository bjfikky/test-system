package com.benorim.testsystem.controller.api.v1;

import com.benorim.testsystem.controller.api.request.RegisterRequest;
import com.benorim.testsystem.controller.api.response.RegisterResponse;
import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthApi {

    private final AuthenticationService authenticationService;

    public AuthApi(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authenticationService.register(registerRequest);
        RegisterResponse response = new RegisterResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
//
//    }
}
