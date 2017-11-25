package me.interview.repository;

import me.interview.entity.Question;

public interface QuestionService {

	void deleteQuestion(Question question) throws Exception;
	
	void updateQuestion(Question question) throws Exception;
}
