package me.interview.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by reimi on 11/19/17.
 */
@Configuration
@ComponentScan(basePackages = {"me.interview.repository"})
@EnableAspectJAutoProxy
public class ServiceConfig {

	@Bean
	ObjectMapper serviceObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_COMMENTS, true)
			  .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
			  .configure(Feature.ALLOW_SINGLE_QUOTES, true);
		return mapper;
	}
}
