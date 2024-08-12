package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Utils.ValidationUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationUtilTest {

    private ValidationUtil validationUtil;

    @BeforeEach
    public void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        validationUtil = new ValidationUtil(validator);
    }

    @Test
    public void testValidate_ValidObject_ShouldReturnEmptyMap() {
        TestObject validObject = new TestObject("ValidName", "ValidDescription");
        Map<String, String> validationErrors = validationUtil.validate(validObject);

        assertEquals(0, validationErrors.size());
    }

    @Test
    public void testValidate_InvalidObject_ShouldReturnValidationErrors() {
        TestObject invalidObject = new TestObject(null, "Short");
        Map<String, String> validationErrors = validationUtil.validate(invalidObject);

        assertEquals(2, validationErrors.size());
        assertEquals("must not be null", validationErrors.get("name"));
        assertEquals("size must be between 10 and 200", validationErrors.get("description"));
    }
    private static class TestObject {
        @NotNull
        private String name;

        @Size(min = 10, max = 200)
        private String description;

        public TestObject(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}