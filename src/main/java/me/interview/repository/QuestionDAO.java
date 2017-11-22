package me.interview.repository;

import org.springframework.data.repository.CrudRepository;

import me.interview.entity.Question;

public interface QuestionDAO extends CrudRepository<Question, Long> {

	
}
