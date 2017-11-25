package me.interview.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.Question;
import me.interview.entity.UserAnswer;
import me.interview.repository.QuestionRepo;
import me.interview.repository.QuestionService;
import me.interview.repository.UserAnswerRepo;

@RestController
public class QuestionController {

	private static Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);
	
	@Autowired
	private QuestionRepo qRepo;
	
	@Autowired
	private QuestionService qService;
	
	@Autowired
	private UserAnswerRepo uaRepo;
	
	@Autowired
	@Qualifier("serviceObjectMapper")
	private ObjectMapper mapper;
	
	@Autowired
	private Validator validator;

	
	@RequestMapping("/")
	ModelAndView questionManager() {
		Iterable<Question> questions = qRepo.findAllJoin();
		Map<String, Object> model = new HashMap<>();
		model.put("questions", questions);
		return new ModelAndView("questionManager", model);
	}
	
	@RequestMapping("/detail/{id}")
	ModelAndView questionDetail(@PathVariable String id) {
		Optional<Question> qResult = qRepo.findById(Long.parseLong(id));
		return qResult.map(q->new ModelAndView("questionDetail", "q", q))
					  .orElse(new ModelAndView("questionDetail", "q", new Question()));		
	}
	
	@RequestMapping("/api/questionUpdate")
	ResponseEntity<Void> questionUpdate(@RequestBody Question question) {
		Set<ConstraintViolation<Question>> constraints = validator.validate(question);
		if(constraints.size() > 0) {
			constraints.forEach(c->LOGGER.warn(c.toString()));
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.badRequest().build();
	}
		
	@RequestMapping(value="/api/allQuestions", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	ResponseEntity<String> allQuestions() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Iterable<Question> all = qRepo.findAllJoin();
		String response = mapper.writeValueAsString(all);
		return ResponseEntity.accepted().body(response);
	}
	
	@RequestMapping(value="/api/deleteQuestion", method=RequestMethod.DELETE, consumes="application/json")
	ResponseEntity<Void> deleteQuestion(@RequestBody Question q, UriComponentsBuilder ucBuilder) throws Exception {
		Optional<Question> result = qRepo.findById(q.getId());
		if(result.isPresent()) {
			Question question = result.get();
			qService.deleteQuestion(question);
			return ResponseEntity.accepted().location(ucBuilder.path("/").build().toUri()).build();
		}
		return ResponseEntity.badRequest().build();
	}
	
	@RequestMapping(value="/api/userAnswer", method=RequestMethod.POST, consumes="application/json")
	ResponseEntity<Void> userAnswer(@RequestBody UserAnswer answer) throws Exception {
		//set relation
		if(answer.getAnswers().isEmpty()) 
			return ResponseEntity.badRequest().build();
		answer.getAnswers().forEach(op->op.setOwner(answer));
		Set<ConstraintViolation<UserAnswer>> constraints = validator.validate(answer);
		if(constraints.size() > 0) {
			constraints.forEach(c->LOGGER.warn(c.toString()));
			return ResponseEntity.badRequest().build();
		}
		try {
			uaRepo.save(answer);
			return ResponseEntity.accepted().build();
		} catch(Exception e) {
			LOGGER.error("Error", e);
			return ResponseEntity.badRequest().build();
		}
	}
	
}
