package com.example.online_shop_api.Utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class ValidationUtil {

    private final Validator validator;

    public Map<String, String> validate(Object obj) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        Map<String, String> validationErrors = new HashMap<>();

        for (ConstraintViolation<Object> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            validationErrors.put(fieldName, errorMessage);
        }

        return validationErrors;
    }
}