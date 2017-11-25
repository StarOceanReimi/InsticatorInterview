package me.interview.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class UserAnswer implements IDAware<Long> {
	
	private static final long serialVersionUID = 4515298855021875097L;

	@Min(1)
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String sourceInfo;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="question_id", nullable=false)
	private Question question;
	
	@Size(min=1)
	@OneToMany(cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER, mappedBy="owner")
	private Set<@Valid UserAnswerOption> answers;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceInfo() {
		return sourceInfo;
	}

	public void setSourceInfo(String sourceInfo) {
		this.sourceInfo = sourceInfo;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Set<UserAnswerOption> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<UserAnswerOption> answers) {
		this.answers = answers;
	}
	
}
