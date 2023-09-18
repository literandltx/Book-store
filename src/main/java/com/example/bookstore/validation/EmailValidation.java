package com.example.bookstore.validation;

import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidation implements ConstraintValidator<Email, String> {
    private static final String PATTERN_OF_EMAIL = "^(.+)@(\\S+)$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return email.length() >= 6 && Pattern.compile(PATTERN_OF_EMAIL).matcher(email).matches();
    }
}
