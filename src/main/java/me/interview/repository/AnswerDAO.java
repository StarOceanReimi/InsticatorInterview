package me.interview.repository;

import org.springframework.data.repository.CrudRepository;

import me.interview.entity.Answer;

public interface AnswerDAO extends CrudRepository<Answer, Long> {

}
