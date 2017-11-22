package me.interview;

import java.io.IOException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.AttributeValue;
import me.interview.springconfig.JPAConfig;
import me.interview.springconfig.ServiceConfig;

public class App {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		AnnotationConfigApplicationContext config = new AnnotationConfigApplicationContext();
		config.register(JPAConfig.class, ServiceConfig.class);
		config.refresh();
		ObjectMapper mapper = config.getBean(ObjectMapper.class);
		AttributeValue value = mapper.readValue("{ name: 'hello', type: 'range', start: 100.0 }", AttributeValue.class);
		System.out.println(value.getClass());
	}
}
