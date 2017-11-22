package interview;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.interview.entity.Answer;
import me.interview.entity.AttributeValue;
import me.interview.entity.Question;
import me.interview.repository.AnswerDAO;
import me.interview.repository.QuestionDAO;
import me.interview.springconfig.JPAConfig;
import me.interview.springconfig.ServiceConfig;
import me.interview.tools.DDLTools;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={JPAConfig.class, ServiceConfig.class})
public class QuestionDAOTest {

	private boolean setupOnce = false;
	
	@Autowired
	QuestionDAO qDao;
	
	@Autowired
	AnswerDAO aDao;
	
	@Autowired
	ObjectMapper serviceMapper;
	
	static List<Question> questionsForInsert;
	
	static List<Answer> answerSheets;
	
	@BeforeClass
	public static void setupDatabase() throws IOException {
		DDLTools.main(null);
	}
	
	@Before
	public void setupData() throws IOException {
		if(setupOnce) return;
		Path newQuestion = Paths.get(System.getProperty("user.dir"), "testdata/questions_new_request");
		InputStream newQuestionStream = Files.newInputStream(newQuestion, StandardOpenOption.READ);
		questionsForInsert = serviceMapper.readValue(newQuestionStream, new TypeReference<List<Question>>(){});
		questionsForInsert.stream().forEach(q->q.getAnswers().forEach(a->a.setQuestion(q)));
		assertThat(questionsForInsert, not(equalTo(null)));
		
		Path answers = Paths.get(System.getProperty("user.dir"), "testdata/questions_user_answer");
		InputStream answersStream = Files.newInputStream(answers, StandardOpenOption.READ);
		answerSheets = serviceMapper.readValue(answersStream, new TypeReference<List<Answer>>(){});
		assertThat(answerSheets, not(equalTo(null)));
		setupOnce = true;
	}
	
	@Test
	public void testOrder() {
		testInsert();
		testAnswer();
	}
	
	public void testInsert() {
		qDao.saveAll(questionsForInsert);
	}
	
	public void testAnswer() {
		List<Long> ids = answerSheets.stream().map(a->a.getId()).collect(toList());
		Map<Long, Set<AttributeValue>> map = answerSheets.stream().collect(toMap(a->a.getId(), a->a.getCheckedValues()));
		Iterable<Answer> syncAnswers = aDao.findAllById(ids);
		for(Answer ans : syncAnswers) {
			ans.setCheckedValues(map.get(ans.getId()));
		}
		aDao.saveAll(syncAnswers);
	}
}
