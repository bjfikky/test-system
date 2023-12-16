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
}
