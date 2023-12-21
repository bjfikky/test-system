package com.benorim.testsystem.repository;

import com.benorim.testsystem.entity.Test;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface TestRepository extends ListCrudRepository<Test, Long> {
    Optional<Test> findByIdAndTestTakerId(Long id, Long testTakerId);
}
