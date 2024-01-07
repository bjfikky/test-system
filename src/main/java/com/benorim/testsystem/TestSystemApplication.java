package com.benorim.testsystem;

import com.benorim.testsystem.entity.User;
import com.benorim.testsystem.enums.Role;
import com.benorim.testsystem.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TestSystemApplication {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public TestSystemApplication(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        final String adminLastName = "admin";
        final String adminFirstName = "admin";
        final String adminEmail = "admin@email.com";
        final String adminPassword = "password1234";
        final String adminEncryptedPassword = this.passwordEncoder.encode(adminPassword);

        try {
            userService.findByEmail(adminEmail);
        } catch (UsernameNotFoundException e) {
            User user = new User(adminFirstName, adminLastName, adminEmail, adminEncryptedPassword, Role.ADMIN);
            this.userService.createUser(user);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TestSystemApplication.class, args);
    }
}
