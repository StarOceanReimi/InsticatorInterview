package interview;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import me.interview.entity.Question;

public class ValidationTest {

	static final ObjectMapper mapper = new ObjectMapper();
	static final Validator validator;
	static {
		ValidatorFactory facotry = Validation.buildDefaultValidatorFactory();
		validator = facotry.getValidator();
		
		JavaTimeModule module = new JavaTimeModule();
		mapper.registerModule(module);
		mapper.configure(Feature.ALLOW_COMMENTS, true)
			  .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
			  .configure(Feature.ALLOW_SINGLE_QUOTES, true)
			  .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}
	
	@Test
	public void testQuestion() throws IOException {
		Path newQuestion = Paths.get(System.getProperty("user.dir"), "testdata/questions_for_validation");
		InputStream newQuestionStream = Files.newInputStream(newQuestion, StandardOpenOption.READ);
		Question[] questions = mapper.readValue(newQuestionStream, Question[].class);
		
		Arrays.stream(questions).forEach(q->{
			Set<ConstraintViolation<Question>> constraintViolations = validator.validate(q);
			constraintViolations.forEach(System.out::println);
			assertThat(constraintViolations.size(), is(equalTo(0)));
		});
	}
}
