package me.interview.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@DiscriminatorValue(value="CATEGORY")
public class Category extends OptionValue {
	
	private static final long serialVersionUID = -6491132286349017021L;

	public Category() {
	}
	
	public Category(String name) {
		super(name);
	}
}
