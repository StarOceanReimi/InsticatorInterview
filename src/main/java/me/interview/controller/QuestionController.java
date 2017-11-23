package me.interview.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.Question;
import me.interview.repository.QuestionRepo;

@RestController
public class QuestionController {

	@RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello";
    }
	
	@RequestMapping("/manager")
	ModelAndView questionManager() {
		Map<String, Object> model = new HashMap<>();
		model.put("name", "Q");
		model.put("lists", Arrays.<String>asList("A", "B", "C", "D", "E"));
		return new ModelAndView("questionManager", model);
	}
	
	@Autowired
	private QuestionRepo qRepo;
	
	@Autowired
	@Qualifier("serviceObjectMapper")
	private ObjectMapper mapper;
	
	@RequestMapping(value="/api/all", method=RequestMethod.GET, produces="application/json")
	ResponseEntity<String> allQuestions() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Iterable<Question> all = qRepo.findAllJoin();
		String response = mapper.writeValueAsString(all);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
}
