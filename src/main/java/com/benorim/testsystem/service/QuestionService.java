package com.benorim.testsystem.service;

import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.exception.InvalidOptionsException;
import com.benorim.testsystem.exception.QuestionNotFoundException;
import com.benorim.testsystem.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question addQuestion(Question question) {
        validateOptions(question.getOptions());
        return questionRepository.save(question);
    }

    public List<Question> addQuestions(List<Question> questions) {
        questions.forEach(question -> validateOptions(question.getOptions()));
        return questionRepository.saveAll(questions);
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) throw new QuestionNotFoundException("Question does not exist");
        questionRepository.deleteById(question.getId());
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    public List<Question> getRandomQuestions(int count) {
        List<Question> shuffledList = new ArrayList<>(getAllQuestions());
        Collections.shuffle(shuffledList);
        return shuffledList.subList(0, Math.min(count, shuffledList.size()));
    }

    protected static void validateOptions(List<Option> options) {
        boolean hasCorrectOption = options.stream().anyMatch(Option::isCorrect);
        long countCorrectOptions = options.stream().filter(Option::isCorrect).count();
        if (!hasCorrectOption || countCorrectOptions > 1) {
            throw new InvalidOptionsException("Exactly one option must be correct");
        }
    }
}
