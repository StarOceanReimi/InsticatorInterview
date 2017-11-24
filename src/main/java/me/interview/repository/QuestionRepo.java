package me.interview.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import me.interview.entity.Question;

public interface QuestionRepo extends CrudRepository<Question, Long> {

	@Query("select q from Question q join fetch q.index qi left join fetch q.column qc")
	Iterable<Question> findAllJoin();
}
