package me.interview.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.annotations.Field;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
			  include=As.PROPERTY,
			  property="type",
			  defaultImpl=Category.class)
@JsonSubTypes({
	@Type(value=Category.class, name="category"),
	@Type(value=Range.class,    name="range")
})
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
public abstract class OptionValue implements IDAware<Long> {
	
	private static final long serialVersionUID = 2274273176519857204L;

	@Min(1)
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="owner_id", nullable = false)
	private OptionGroup owner;
	
	private boolean suggested;
	
	@Field
	@NotBlank
	@Column(nullable = false)
	private String name;
	
	@JsonIgnore
	@OneToMany(mappedBy="index", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<UserAnswerOption> answerOptionIndexRefs;
	
	@JsonIgnore
	@OneToMany(mappedBy="column", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<UserAnswerOption> answerOptionColumnRefs;
	
	public OptionValue() {
	}
	
	public OptionValue(String name) {
		super();
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OptionGroup getOwner() {
		return owner;
	}

	public void setOwner(OptionGroup owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSuggested() {
		return suggested;
	}

	public void setSuggested(boolean suggested) {
		this.suggested = suggested;
	}

	public Set<UserAnswerOption> getAnswerOptionIndexRefs() {
		return answerOptionIndexRefs;
	}

	public void setAnswerOptionIndexRefs(Set<UserAnswerOption> answerOptionIndexRefs) {
		this.answerOptionIndexRefs = answerOptionIndexRefs;
	}

	public Set<UserAnswerOption> getAnswerOptionColumnRefs() {
		return answerOptionColumnRefs;
	}

	public void setAnswerOptionColumnRefs(Set<UserAnswerOption> answerOptionColumnRefs) {
		this.answerOptionColumnRefs = answerOptionColumnRefs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
