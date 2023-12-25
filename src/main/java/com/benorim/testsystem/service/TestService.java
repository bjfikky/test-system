package com.benorim.testsystem.service;

import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.entity.Test;
import com.benorim.testsystem.exception.InvalidOptionsException;
import com.benorim.testsystem.exception.InvalidTestException;
import com.benorim.testsystem.repository.OptionRepository;
import com.benorim.testsystem.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final OptionRepository optionRepository;

    public TestService(TestRepository testRepository, OptionRepository optionRepository) {
        this.testRepository = testRepository;
        this.optionRepository = optionRepository;
    }

    public Test createTest(Test test) {
        return testRepository.save(test);
    }



    public Test getTestByIdAndTestTakerId(Long testId, Long testTakerId) {
        Test test = testRepository.findByIdAndTestTakerId(testId, testTakerId).orElse(null);
        if (test == null) throw new InvalidTestException("Test does not exist for test taker");
        return test;
    }

    /**
     *
     * @param testId the Id of the Test
     * @param testTakerId the Id of the TestTaker
     * @param selectedOptionsIds the selected Options Ids
     * @return saves and returns the updated Test
     */
    public Test submitTestAnswers(Long testId, Long testTakerId, List<Long> selectedOptionsIds) {
        Test test = getTestByIdAndTestTakerId(testId, testTakerId);

        validateSubmittedTest(testId, testTakerId, selectedOptionsIds, test);

        List<Option> options = optionRepository.findAllById(selectedOptionsIds);
        long countCorrectOptions = options.stream().filter(Option::isCorrect).count();
        double percentage = ((double) countCorrectOptions / selectedOptionsIds.size()) * 100;

        test.setPercentScore(percentage);
        test.setCompleted(true);
        test.setDateCompleted(new Date());

        return testRepository.save(test);
    }

    private static void validateSubmittedTest(Long testId, Long testTakerId, List<Long> selectedOptionsIds, Test test) {
        if (test.isCompleted()) {
            throw new InvalidTestException("Test already completed");
        }
        if (!test.getId().equals(testId)
                || !test.getTestTaker().getId().equals(testTakerId)) {
            throw new InvalidTestException("Hmm... Something smells fishy");
        }
        if (test.getQuestions().size() != selectedOptionsIds.size()) {
            throw new InvalidOptionsException("Cannot submit. Not all questions were answered.");
        }

        Set<Long> idsOfTestTaken = new HashSet<>();
        test.getQuestions().forEach(question ->
                question.getOptions().stream()
                        .map(Option::getId)
                        .forEach(idsOfTestTaken::add)
        );

        if (!idsOfTestTaken.containsAll(selectedOptionsIds)) {
            throw new InvalidOptionsException("Cannot submit. You have options that don't belong to test questions.");
        }
    }
}
