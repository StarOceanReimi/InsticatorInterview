package me.interview.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import me.interview.entity.Tag;

public class TagConstraints implements ConstraintValidator<TagValidator, Tag>{

	@Override
	public boolean isValid(Tag value, ConstraintValidatorContext context) {
		
		return !(value.getId() == null && value.getName() == null);
	}

}
