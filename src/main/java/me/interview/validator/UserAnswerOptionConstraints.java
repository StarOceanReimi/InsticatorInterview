package me.interview.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import me.interview.entity.UserAnswerOption;

public class UserAnswerOptionConstraints implements ConstraintValidator<UserAnswerOptionValidator, UserAnswerOption> {

	@Override
	public boolean isValid(UserAnswerOption value, ConstraintValidatorContext context) {
		if(value.getIndex() == null) return false;
		Long indexId = value.getIndex().getId();
		if(indexId == null || indexId < 1) return false;
		if(value.getColumn() != null) {
			Long columnId = value.getColumn().getId();
			if(columnId == null || columnId < 1) return false;
		}
		return true;
	}

}
