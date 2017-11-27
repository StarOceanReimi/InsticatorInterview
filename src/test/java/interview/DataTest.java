package interview;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.Question;
import me.interview.entity.UserAnswer;
import me.interview.repository.QuestionRepo;
import me.interview.repository.QuestionService;
import me.interview.repository.UserAnswerRepo;
import me.interview.springconfig.JPAConfig;
import me.interview.springconfig.ServiceConfig;
import me.interview.tools.DDLTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={JPAConfig.class, ServiceConfig.class})
public class DataTest {

	private boolean setupOnce = false;
	
	@Autowired
	QuestionRepo qDao;
	
	@Autowired
	UserAnswerRepo uaDao;
	
	@Autowired
	QuestionService qService;
	
	@Autowired
	ObjectMapper serviceMapper;
	
	static List<Question> questionsForInsert;
	
	static List<UserAnswer> userAnswersForInsert;
	
	static List<Question> questionsForUpdate;
	
	@BeforeClass
	public static void setupDatabase() throws IOException {
		DDLTools.main(null);
	}
	
	@Before
	public void setupData() throws IOException {
		if(setupOnce) return;
		Path questionsForInsertFile = Paths.get(System.getProperty("user.dir"), "testdata/questions_new_request");
		InputStream insertStream = Files.newInputStream(questionsForInsertFile, StandardOpenOption.READ);
		questionsForInsert = serviceMapper.readValue(insertStream, new TypeReference<List<Question>>(){});
		
		Path userAnswers = Paths.get(System.getProperty("user.dir"), "testdata/questions_user_answer");
		InputStream userAnswerStream = Files.newInputStream(userAnswers, StandardOpenOption.READ);
		userAnswersForInsert = serviceMapper.readValue(userAnswerStream, new TypeReference<List<UserAnswer>>() {});
		
		Path questionsForUpdateFile = Paths.get(System.getProperty("user.dir"), "testdata/questions_change_request");
		InputStream updateStream = Files.newInputStream(questionsForUpdateFile, StandardOpenOption.READ);
		questionsForUpdate = serviceMapper.readValue(updateStream, new TypeReference<List<Question>>(){});
		
	}
	
	
	@Test
	public void test0InsertNewQuestions() {
		for(Question question : questionsForInsert) {
			question.setCreateTime(LocalDateTime.now());
			question.getIndex().getOptions().forEach(op->op.setOwner(question.getIndex()));
			if(question.getColumn() != null) {
				question.getColumn().getOptions().forEach(op->op.setOwner(question.getColumn()));
			}
		}
		qDao.saveAll(questionsForInsert);
	}
	
	@Test
	public void testSelect() {
		Iterable<Question> iter = qDao.findAllJoin();
		iter.forEach(q->{
			try {
				String json = serviceMapper.writerWithDefaultPrettyPrinter().writeValueAsString(q);
				System.out.println(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Test
	public void testInsertUserAnswers() {
		for(UserAnswer answer : userAnswersForInsert) {
			answer.getAnswers().forEach(op->op.setOwner(answer));
		}
		uaDao.saveAll(userAnswersForInsert);
	}
	
	@Test
	public void testUpdateQuestion() throws Exception {
		for(Question question : questionsForUpdate) {
			question.setCreateTime(LocalDateTime.now());
			qService.updateQuestion(question);
		}
	}
}
