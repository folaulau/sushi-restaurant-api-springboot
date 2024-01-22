package com.sushi.api.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.sushi.api.utils.ValidationUtils;

public class EmailValidator implements ConstraintValidator<Email, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ValidationUtils.isValidEmailFormat(value);
	}

}
