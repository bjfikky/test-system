package com.benorim.testsystem.service;

import com.benorim.testsystem.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();
    User createUser(User user);
    User findByEmail(String email);
}
