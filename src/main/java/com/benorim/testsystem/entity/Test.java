package com.benorim.testsystem.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;
import java.util.List;

@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean completed;
    @Temporal(TemporalType.DATE)
    private Date dateAdded;
    @Temporal(TemporalType.DATE)
    private Date dateCompleted;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Question> questions;
    @OneToOne
    @JoinColumn(name = "test_taker_id")
    private TestTaker testTaker;
    private Double percentScore;

    public Test() {
    }

    public Test(List<Question> questions, TestTaker testTaker) {
        this.questions = questions;
        this.testTaker = testTaker;
        this.completed = false;
        this.dateAdded = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public TestTaker getTestTaker() {
        return testTaker;
    }

    public void setTestTaker(TestTaker testTaker) {
        this.testTaker = testTaker;
    }

    public Double getPercentScore() {
        return percentScore;
    }

    public void setPercentScore(double percentScore) {
        this.percentScore = percentScore;
    }
}
