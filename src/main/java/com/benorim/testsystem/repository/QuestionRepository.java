package com.benorim.testsystem.repository;

import com.benorim.testsystem.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends ListCrudRepository<Question, Long> {
}
