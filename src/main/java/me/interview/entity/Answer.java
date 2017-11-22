package me.interview.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Answer implements IDAware<Long> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="question_id", nullable=false)
	private Question question;
	
	@ManyToOne(cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER)
	@JoinColumn(name="attribute_id", referencedColumnName="id", nullable=false)
	private Attribute index;

	@ManyToMany(cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER)
	@JoinTable(
		name="AnswerColumns",
		joinColumns=@JoinColumn(name="answer_id", referencedColumnName="id", nullable=false),
		inverseJoinColumns=@JoinColumn(name="attribute_id", referencedColumnName="id", nullable=false)
	)
	private Set<Attribute> columns;
	
	@ManyToMany(cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER)
	@JoinTable(
		name="AnswerChoice",
		joinColumns=@JoinColumn(name="answer_id", referencedColumnName="id", nullable=false),
		inverseJoinColumns=@JoinColumn(name="attribute_value_id", referencedColumnName="id", nullable=false)
	)
	private Set<AttributeValue> checkedValues;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Attribute getIndex() {
		return index;
	}

	public void setIndex(Attribute index) {
		this.index = index;
	}
	
	public Question getQuestion() {
		return question;
	}
	
	public void setQuestion(Question question) {
		this.question = question;
	}

	public Set<Attribute> getColumns() {
		return columns;
	}

	public void setColumns(Set<Attribute> columns) {
		this.columns = columns;
	}

	public Set<AttributeValue> getCheckedValues() {
		return checkedValues;
	}

	public void setCheckedValues(Set<AttributeValue> checkedValues) {
		this.checkedValues = checkedValues;
	}
}
