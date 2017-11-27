package me.interview.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import me.interview.validator.UserAnswerOptionValidator;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@UserAnswerOptionValidator
@Entity
public class UserAnswerOption implements IDAware<Long> {

	private static final long serialVersionUID = 7067020478072776356L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="index_id", nullable=false)
	private OptionValue index;

	@ManyToOne
	@JoinColumn(name="column_id")
	private OptionValue column;
	
	@ManyToOne
	@JoinColumn(name="owner_id", nullable=false)
	private UserAnswer owner;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OptionValue getIndex() {
		return index;
	}

	public void setIndex(OptionValue index) {
		this.index = index;
	}

	public OptionValue getColumn() {
		return column;
	}

	public void setColumn(OptionValue column) {
		this.column = column;
	}

	public UserAnswer getOwner() {
		return owner;
	}

	public void setOwner(UserAnswer owner) {
		this.owner = owner;
	}
	
}
