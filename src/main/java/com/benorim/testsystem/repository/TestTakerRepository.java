package com.benorim.testsystem.repository;

import com.benorim.testsystem.entity.TestTaker;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTakerRepository extends ListCrudRepository<TestTaker, Long> {
}
