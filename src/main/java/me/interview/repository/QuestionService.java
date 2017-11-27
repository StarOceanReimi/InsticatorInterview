package me.interview.repository;

import me.interview.entity.Question;

public interface QuestionService {
	
	void deleteQuestion(Question question) throws Exception;
	
	void updateQuestion(Question question) throws Exception;
	
	void removeOptionGroup(Long questionId, Long groupId) throws Exception;
	
	void removeOptionValue(final Long groupId, final Long opid) throws Exception;
}
