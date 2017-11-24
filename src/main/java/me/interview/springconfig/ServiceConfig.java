package me.interview.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import me.interview.repository.QuestionService;
import me.interview.repository.QuestionServiceImpl;

/**
 * Created by reimi on 11/19/17.
 */
@Configuration
@ComponentScan(basePackages = {"me.interview.repository"})
@EnableAspectJAutoProxy
public class ServiceConfig {

	@JsonFilter("nameExcludeFilter")
	public static class DynamicPropertyFilterMixin {}
	
	@Bean
	ObjectMapper serviceObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		JavaTimeModule module = new JavaTimeModule();
		mapper.registerModule(module);
		mapper.configure(Feature.ALLOW_COMMENTS, true)
			  .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
			  .configure(Feature.ALLOW_SINGLE_QUOTES, true)
			  .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return mapper;
	}
	
	@Bean
	QuestionService questionService() {
		return new QuestionServiceImpl();
	}
}
