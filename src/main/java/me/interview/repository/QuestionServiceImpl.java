package me.interview.repository;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import me.interview.entity.Question;

public class QuestionServiceImpl implements QuestionService {

	@Autowired
	QuestionRepo qRepo;
	
	@Autowired
	UserAnswerRepo uaRepo;
	
	@Autowired
	EntityManager em;
	
	@Transactional
	@Override
	@SuppressWarnings({"rawtypes"})
	public void deleteQuestion(Question question) throws Exception {
		Optional<Question> syncQuestion = qRepo.findById(question.getId());
		if(syncQuestion.isPresent()) {
			question = syncQuestion.get();
		 	Query selectUA = em.createQuery("select ua.id from UserAnswer ua where ua.question.id = ?1");
		 	List ids = selectUA.setParameter(1, question.getId()).getResultList();
		 	if(!ids.isEmpty()) {
		 		Query deleteUAO = em.createQuery("delete from UserAnswerOption uao where uao.owner.id in (?1)");
		 		deleteUAO.setParameter(1, ids).executeUpdate();
		 	}
			uaRepo.deleteUserAnswerByQuestionId(question.getId());
			qRepo.delete(question);
		}
	}

	@Transactional
	@Override
	public void updateQuestion(Question question) throws Exception {
		Optional<Question> syncQuestion = qRepo.findById(question.getId());
		if(syncQuestion.isPresent()) {
			Question dbQuestion = syncQuestion.get();
			BeanUtils.copyProperties(question, dbQuestion);
			qRepo.save(dbQuestion);
		}
	}

}
