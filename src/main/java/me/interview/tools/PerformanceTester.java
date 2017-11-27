package me.interview.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
public class PerformanceTester {

	//# of client submit their answers to server at same time
	static final int NUM_OF_CLIENTS = 100000;
	
	static final String API_URL = "api/userAnswer";
	
	//given a constant seed to avoid changes
	static final Random RANDOM = new Random(1000);
	
	static final String TEST_FILE_LOC = "testdata/questions_user_answer"; 

	static AtomicLong slowUserTotalTime = new AtomicLong(0);
	
	static AtomicInteger counter = new AtomicInteger(0);
	
	static final int threshold = 500;
	
	static class TestRunner implements Runnable {

		final RestTemplate template;
		
		final HttpEntity<String> param;
		
		final String api;
		public TestRunner(String api, RestTemplate template, HttpEntity<String> param) {
			this.api = api;
			this.template = template;
			this.param = param;
		}
		
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			ResponseEntity<String> response = template.postForEntity(api, param, String.class);
			long endTime = System.currentTimeMillis();
			assert response.getStatusCode() == HttpStatus.ACCEPTED;
			long timespend = endTime - startTime;
			if(timespend > threshold) {
				counter.incrementAndGet();
				slowUserTotalTime.addAndGet(timespend);
			}
		}
	}
	
	static String randomBody(final String[] params) {
		return params[RANDOM.nextInt(params.length)];
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		String host = null;
		if(args.length > 0) {
			host = args[0];
		} else {
			host = "http://localhost:8080/";
		}
		String apiAddress = host+API_URL;
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
		System.out.println("start testing...");
		for(int i=0; i<NUM_OF_CLIENTS; i++) {
			service.execute(new TestRunner(apiAddress, 
							userAnswerTemplate, 
							new HttpEntity<String>(randomBody(parameters), headers)));
		}
		
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		System.out.printf("server handles %d of requests in %d ms.\n", NUM_OF_CLIENTS, System.currentTimeMillis()-startTime);
		System.out.printf("%d of clients will get reponsed over %dms, and their avg response time is %.2fms", 
						   counter.get(), threshold, (double)slowUserTotalTime.get()/counter.get());

	}
}
