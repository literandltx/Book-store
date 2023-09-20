package com.example.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    private static final String ISBN_PATTERN = "^\\d{10,13}$";

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        return isbn != null && Pattern.compile(ISBN_PATTERN).matcher(isbn).matches();
    }

    /*
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        char[] isbn = value.toCharArray();
        int sum = 0;

        if (isbn.length == 10) {
            for (int i = 0; i < 10; i++) {
                sum += i * isbn[i];
            }

            return isbn[9] == sum % 11;
        } else {
            if (isbn.length == 13) {
                for (int i = 0; i < 12; i++) {
                    sum += i % 2 == 0 ? isbn[i] : 3 * isbn[i];
                }

                return isbn[12] == 10 - (sum % 10);
            }
        }

        return false;
    }
     */
}
