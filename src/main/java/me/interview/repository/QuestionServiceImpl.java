package me.interview.repository;

import static me.interview.tools.ExceptionHelper.customMessage;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.OptionGroup;
import me.interview.entity.OptionValue;
import me.interview.entity.Question;

public class QuestionServiceImpl implements QuestionService {

	@Autowired
	QuestionRepo qRepo;
	
	@Autowired
	UserAnswerRepo uaRepo;
	
	@Autowired
	OptionGroupRepo gRepo;
	
	@Autowired
	OptionValueRepo opRepo;
	
	@Autowired
	UserAnswerOptionRepo uaoRepo;
	
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
			uaRepo.deleteByQuestionId(question.getId());
			qRepo.delete(question);
		}
	}

	@Autowired
	ObjectMapper mapper;
	
	@Transactional
	@Override
	public void updateQuestion(Question question) throws Exception {
		Question dbQuestion = qRepo.findJoin(question.getId()).orElseThrow(customMessage("question not exists"));
		BeanUtils.copyProperties(question, dbQuestion);
		qRepo.save(dbQuestion);
	}

	@Transactional
	@Override
	public void removeOptionValue(final Long groupId, final Long opid) throws Exception {
		OptionGroup group = gRepo.findById(groupId).orElseThrow(customMessage("option group not exists"));
		OptionValue option = group.getOptions().stream().filter(op -> op.getId() == opid)
				 				  .findAny().orElseThrow(customMessage("option value not exists"));
		group.getOptions().remove(option);
		em.createQuery("delete from OptionValue op where op.id = ?1")
		  .setParameter(1, option.getId()).executeUpdate();
	}
	
	@Transactional
	@Override
	public void removeOptionGroup(Long questionId, Long groupId) throws Exception {
		Question dbQuestion = qRepo.findById(questionId).orElseThrow(customMessage("id[%d] not exists.", questionId));
		if(dbQuestion.getIndex().getId() == groupId) 
			throw customMessage("can not remove index group from question").get();
		if(dbQuestion.getColumn() == null || dbQuestion.getColumn().getId() != groupId)
			throw customMessage("can not remove unrelated group from question").get();
		dbQuestion.setColumn(null);
		OptionGroup group = gRepo.findById(groupId).orElse(null);
		opRepo.deleteAll(group.getOptions());
		gRepo.delete(group);
		
	}
}
