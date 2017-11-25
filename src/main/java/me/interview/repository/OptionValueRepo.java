package me.interview.repository;

import org.springframework.data.repository.CrudRepository;

import me.interview.entity.OptionValue;

public interface OptionValueRepo extends CrudRepository<OptionValue, Long> {

}
