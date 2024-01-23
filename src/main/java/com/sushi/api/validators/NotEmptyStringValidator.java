package com.sushi.api.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyStringValidator implements ConstraintValidator<NotEmptyString, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value==null) {
			return false;
		}
		if(value.trim().length()<=0) {
			return false;
		}
		return true;
	}
}
