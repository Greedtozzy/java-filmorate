package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TextValidator implements ConstraintValidator<CustomTextAnnotation, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return !value.contains(" ");
        }
        return true;
    }
}