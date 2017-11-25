package me.interview.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UserAnswerOptionConstraints.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserAnswerOptionValidator {
	 String message() default "user answer option invalid";
	 
	 Class<?>[] groups() default {};
	 
	 Class<? extends Payload>[] payload() default { };
}
