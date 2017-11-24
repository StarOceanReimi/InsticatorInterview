package me.interview.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import me.interview.entity.UserAnswer;

public interface UserAnswerRepo extends CrudRepository<UserAnswer, Long> {

	@Modifying
	@Query("delete from UserAnswer ua where ua.question.id = ?1")
	void deleteUserAnswerByQuestionId(Long id);
}
