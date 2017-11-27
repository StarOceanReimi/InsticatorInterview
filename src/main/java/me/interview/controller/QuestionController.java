package me.interview.controller;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.Question;
import me.interview.entity.UserAnswer;
import me.interview.repository.OptionValueRepo;
import me.interview.repository.QuestionLuceneSearchService;
import me.interview.repository.QuestionRepo;
import me.interview.repository.QuestionService;
import me.interview.repository.UserAnswerRepo;
import me.interview.tools.FormDataParser;

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
	private OptionValueRepo opRepo;
	
	@Autowired
	@Qualifier("serviceObjectMapper")
	private ObjectMapper mapper;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private QuestionLuceneSearchService fuzzySearchService;

	
	@RequestMapping("/")
	ModelAndView questionManager(@RequestParam MultiValueMap<String, String> parameters) {
		ModelAndView mv = new ModelAndView("questionManager");
		String term = parameters.getFirst("term");
		Iterable<Question> questions = null;
		if(term == null || term.length() == 0) {
			questions = qRepo.findAllJoin();
		} else {
			try {
				questions = fuzzySearchService.fuzzySearch(term, 1);
			} catch (Exception e) {
				//backup solution
				LOGGER.warn("FUZZY SEARCH ERROR: {}", e.getMessage());
				questions = qRepo.findAllJoin();
			}
			
		}
		mv.addObject("questions", questions);
		return mv;
	}
	
	@RequestMapping("/detail/{id}")
	ModelAndView questionDetail(@PathVariable String id) {
		return qRepo.findById(Long.parseLong(id))
					  .map(q->new ModelAndView("questionDetail", "q", q))
					  .orElse(new ModelAndView("questionDetail", "q", new Question()));		
	}
	
	@RequestMapping(value="/api/questionUpdate", consumes="application/json", method=RequestMethod.POST)
	ResponseEntity<Void> questionUpdate(@RequestBody Question question, UriComponentsBuilder ucBuilder) {
		Set<ConstraintViolation<Question>> constraints = validator.validate(question);
		if(constraints.size() > 0) {
			constraints.forEach(c->LOGGER.warn(c.toString()));
			return ResponseEntity.badRequest().build();
		}
		try {
			question.setCreateTime(LocalDateTime.now());
			if(question.getId() == null) {
				question.getIndex().getOptions().forEach(op->op.setOwner(question.getIndex()));
				if(question.getColumn() != null) {
					question.getColumn().getOptions().forEach(op->op.setOwner(question.getColumn()));
				}
				qRepo.save(question);
			} else {
				qService.updateQuestion(question);
			}	
			return ResponseEntity.accepted().location(ucBuilder.path("/").build().toUri()).build();
		} catch (Exception ex) {
			LOGGER.error("Error", ex);
			return ResponseEntity.badRequest().build();
		}
	}
	
	@RequestMapping(value="/api/removeOptionGroup", method=RequestMethod.DELETE)
	ResponseEntity<Void> removeOptionGroup(@RequestBody String body) {
		try {
			MultiValueMap<String, String> map = FormDataParser.parse(body);
			Long questionId = Long.parseLong(map.getFirst("qid"));
			Long groupId  = Long.parseLong(map.getFirst("gid"));
			qService.removeOptionGroup(questionId, groupId);
			return ResponseEntity.accepted().build();
		} catch (Exception ex) {
			LOGGER.error("Error", ex);
			return ResponseEntity.badRequest().build();
		}
	}
	
	@RequestMapping(value="/api/removeOptionValue", method=RequestMethod.DELETE)
	ResponseEntity<Void> removeOptionValue(@RequestBody String body) {
		try {
			MultiValueMap<String, String> map = FormDataParser.parse(body);
			Long opid = Long.parseLong(map.getFirst("opid"));
			Long gid  = Long.parseLong(map.getFirst("gid"));
			qService.removeOptionValue(gid, opid);
			return ResponseEntity.accepted().build();
		} catch (Exception ex) {
			LOGGER.error("Error", ex);
			return ResponseEntity.badRequest().build();
		}
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
	
	@RequestMapping(value="/api/userAnswer", method=RequestMethod.POST, produces="application/json", consumes="application/json")
	ResponseEntity<String> userAnswer(@RequestBody final UserAnswer answer) throws Exception {
		if(answer.getAnswers().isEmpty()) 
			return ResponseEntity.badRequest().build();
		//set relation
		answer.getAnswers().forEach(op->op.setOwner(answer));
		Set<ConstraintViolation<UserAnswer>> constraints = validator.validate(answer);
		if(constraints.size() > 0) {
			constraints.forEach(c->LOGGER.warn(c.toString()));
			return ResponseEntity.badRequest().build();
		}
		try {
			uaRepo.save(answer);

			List<Long> indexIds = answer.getAnswers().stream()
										.map(uao->uao.getIndex().getId()).collect(toList());
			List<Long> columnIds = answer.getAnswers().stream().filter(uao->uao.getColumn() != null)
										 .map(uao->uao.getColumn().getId()).collect(toList());
			
			boolean rightAnswer = stream(opRepo.findAllById(indexIds).spliterator(), false)
											   .allMatch(op->op.isSuggested());
			if(columnIds.size() > 0 && rightAnswer) {
				rightAnswer = stream(opRepo.findAllById(columnIds).spliterator(), false)
						   		.allMatch(op->op.isSuggested());
			}
			
			return ResponseEntity.accepted().body(String.format("{ \"answerRight\" : %s }", rightAnswer));
		} catch(Exception e) {
			LOGGER.error("Error", e);
			return ResponseEntity.badRequest().build();
		}
	}
	
}
