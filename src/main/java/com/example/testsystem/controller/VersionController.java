package com.example.testsystem.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VersionController {

    @Value("${project.version}")
    private String projectVersion;

    @GetMapping("/version")
    public String getProjectVersion() {
        return "version: " + projectVersion;
    }
}

