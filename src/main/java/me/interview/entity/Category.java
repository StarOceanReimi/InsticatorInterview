package me.interview.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CATEGORY")
public class Category extends AttributeValue {

	public Category() {
	}
}
