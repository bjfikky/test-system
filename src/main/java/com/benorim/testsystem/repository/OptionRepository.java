package com.benorim.testsystem.repository;

import com.benorim.testsystem.entity.Option;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends ListCrudRepository<Option, Long> {
}
