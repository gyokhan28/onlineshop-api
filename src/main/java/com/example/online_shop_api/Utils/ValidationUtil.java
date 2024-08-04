package com.example.online_shop_api.Utils;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class ValidationUtil {
    public List<String> validateNotNullFields(Object obj) {
        List<String> validationErrors = new ArrayList<>();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(NotNull.class)) {
                NotNull notNullAnnotation = field.getAnnotation(NotNull.class);
                try {
                    if (field.get(obj) == null) {
                        validationErrors.add(field.getName() + ": " + notNullAnnotation.message());
                    }
                } catch (IllegalAccessException e) {
                    validationErrors.add("Error accessing field: " + field.getName());
                }
            }
        }

        return validationErrors;
    }
}