package com.benorim.testsystem.service;

import com.benorim.testsystem.entity.TestTaker;
import com.benorim.testsystem.repository.TestTakerRepository;
import org.springframework.stereotype.Service;

@Service
public class TestTakerService {

    private final TestTakerRepository testTakerRepository;

    public TestTakerService(TestTakerRepository testTakerRepository) {
        this.testTakerRepository = testTakerRepository;
    }

    public TestTaker createTestTaker(String username) {
        return testTakerRepository.save(new TestTaker(username));
    }

    public TestTaker getTestTaker(Long id) {
        return testTakerRepository.findById(id).orElse(null);
    }

}
