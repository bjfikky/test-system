package com.benorim.testsystem.service;

import com.benorim.testsystem.entity.Option;
import com.benorim.testsystem.entity.Question;
import com.benorim.testsystem.exception.InvalidOptionsException;
import com.benorim.testsystem.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionService {

    public final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question addQuestion(Question question) {
        boolean hasCorrectOption = question.getOptions().stream().anyMatch(Option::isCorrect);
        if (!hasCorrectOption) {
            throw new InvalidOptionsException("One option must be correct");
        }
        return questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }
}
