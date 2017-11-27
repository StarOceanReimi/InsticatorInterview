package interview;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.UserAnswer;


/**
 * Test the /api/userAnswer function
 * To see whether it process large number of requests at
 * the same time, since it is the main api that will be
 * used by many clients
 * @author Li
 *
 */
public class ServerLoadTest {

	//# of client submit their answers to server at same time
	static final int NUM_OF_CLIENTS = 100000;
	
	static final String API_URL = "http://localhost:8080/api/userAnswer";
	
	//given a constant seed to avoid changes
	static final Random RANDOM = new Random(1000);
	
	static final String TEST_FILE_LOC = "testdata/questions_user_answer"; 

	static AtomicInteger counter = new AtomicInteger(0);
	
	static class TestRunner implements Runnable {

		final RestTemplate template;
		
		final HttpEntity<String> param;
		
		public TestRunner(RestTemplate template, HttpEntity<String> param) {
			this.template = template;
			this.param = param;
		}
		
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			ResponseEntity<String> response = template.postForEntity(API_URL, param, String.class);
			long endTime = System.currentTimeMillis();
			assert response.getStatusCode() == HttpStatus.ACCEPTED;
			if(endTime - startTime > 1500)
				counter.incrementAndGet();
		}
		
	}
	
	static String randomBody(final String[] params) {
		return params[RANDOM.nextInt(params.length)];
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final RestTemplate userAnswerTemplate = new RestTemplate();
		Path testFile = Paths.get(System.getProperty("user.dir"), TEST_FILE_LOC);
		ObjectMapper mapper = new ObjectMapper()
									.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
									.configure(Feature.ALLOW_SINGLE_QUOTES, true)
									.configure(Feature.ALLOW_COMMENTS, true);
		
		List<UserAnswer> answers = mapper.readValue(testFile.toFile(), new TypeReference<List<UserAnswer>>() {});
		
		String[] parameters = answers.stream().map(ua->{
			try {
				return mapper.writeValueAsString(ua);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}).toArray(String[]::new);
		
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ExecutorService service = Executors.newFixedThreadPool(100);
		long startTime = System.currentTimeMillis();
		for(int i=0; i<NUM_OF_CLIENTS; i++) {
			service.execute(new TestRunner(userAnswerTemplate, new HttpEntity<String>(randomBody(parameters), headers)));
		}
		
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		System.out.printf("%d of requests takes %d ms.\n", NUM_OF_CLIENTS, System.currentTimeMillis()-startTime);
		System.out.printf("%d of request will be reponse over 1500ms", counter.get());
	}
}
