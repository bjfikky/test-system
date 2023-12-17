package com.benorim.testsystem.service;

import com.benorim.testsystem.entity.Test;
import com.benorim.testsystem.entity.TestTaker;
import com.benorim.testsystem.repository.TestRepository;
import com.benorim.testsystem.repository.TestTakerRepository;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestTakerRepository testTakerRepository;
    private final TestRepository testRepository;

    public TestService(TestTakerRepository testTakerRepository, TestRepository testRepository) {
        this.testTakerRepository = testTakerRepository;
        this.testRepository = testRepository;
    }

    public Test createTest(Test test) {
        return testRepository.save(test);
    }

    public TestTaker createTestTaker(String username) {
        return testTakerRepository.save(new TestTaker(username));
    }

    public Test getTest(Long id) {
        return testRepository.findById(id).orElse(null);
    }

    public Test getTestByIdAndTestTakerId(Long testId, Long testTakerId) {
        return testRepository.findByIdAndTestTakerId(testId, testTakerId).orElse(null);
    }

    public TestTaker getTestTaker(Long id) {
        return testTakerRepository.findById(id).orElse(null);
    }
}
