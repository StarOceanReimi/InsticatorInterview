package me.interview.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

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
public class AttributeValue implements IDAware<Long> {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="owner_id", nullable = false)
	private Attribute owner;
	
	private boolean suggested;
	
	@Column(nullable = false)
	private String name;

	public AttributeValue() {
	}
	
	public AttributeValue(String name) {
		super();
		this.name = name;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Attribute getOwner() {
		return owner;
	}

	public void setOwner(Attribute owner) {
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
}
