package com.sushi.api.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.sushi.api.utils.ValidationUtils;

public class EmailValidator implements ConstraintValidator<Email, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ValidationUtils.isValidEmailFormat(value);
	}

}
