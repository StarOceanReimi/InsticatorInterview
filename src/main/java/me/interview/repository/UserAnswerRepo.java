package me.interview.repository;

import org.springframework.data.repository.CrudRepository;

import me.interview.entity.UserAnswer;

public interface UserAnswerRepo extends CrudRepository<UserAnswer, Long> {

}
