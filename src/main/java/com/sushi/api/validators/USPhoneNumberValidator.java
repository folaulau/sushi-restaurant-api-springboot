package com.sushi.api.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.sushi.api.utils.ValidationUtils;

public class USPhoneNumberValidator implements ConstraintValidator<USPhoneNumber, Long> {

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		return ValidationUtils.isPhoneNumberValid(value);
	}

}
