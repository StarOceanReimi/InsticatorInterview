package me.interview.repository;

import org.springframework.data.repository.CrudRepository;

import me.interview.entity.UserAnswerOption;

public interface UserAnswerOptionRepo extends CrudRepository<UserAnswerOption, Long> {

}
