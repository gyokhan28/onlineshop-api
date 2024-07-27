package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Utils.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationUtilTests {
    private ValidationUtil validationUtil;

    @BeforeEach
    public void setUp(){
        validationUtil = new ValidationUtil();
    }

    @Test
    void testValidateNotNullFields_NoNullFields() {
        class TestObject {
            @NotNull(message = "Field must not be null")
            private final String field1 = "value";

            @NotNull(message = "Field must not be null")
            private final Integer field2 = 1;
        }

        TestObject testObject = new TestObject();
        List<String> errors = validationUtil.validateNotNullFields(testObject);

        assertEquals(0, errors.size(), "Expected no validation errors.");
    }

    @Test
    void testValidateNotNullFields_OneNullField() {
        class TestObject {
            @NotNull(message = "Field must not be null")
            private final String field1 = null;

            @NotNull(message = "Field must not be null")
            private final Integer field2 = 1;
        }

        TestObject testObject = new TestObject();
        List<String> errors = validationUtil.validateNotNullFields(testObject);

        assertEquals(1, errors.size(), "Expected one validation error.");
        assertEquals("field1: Field must not be null", errors.get(0), "Unexpected validation error message.");
    }

    @Test
    void testValidateNotNullFields_MultipleNullFields() {
        class TestObject {
            @NotNull(message = "Field must not be null")
            private final String field1 = null;

            @NotNull(message = "Field must not be null")
            private final Integer field2 = null;
        }

        TestObject testObject = new TestObject();
        List<String> errors = validationUtil.validateNotNullFields(testObject);

        assertEquals(2, errors.size(), "Expected two validation errors.");
        assertEquals("field1: Field must not be null", errors.get(0), "Unexpected validation error message.");
        assertEquals("field2: Field must not be null", errors.get(1), "Unexpected validation error message.");
    }

    @Test
    void testValidateNotNullFields_NoFieldsAnnotated() {
        class TestObject {
            private final String field1 = "value";
            private final Integer field2 = 1;
        }

        TestObject testObject = new TestObject();
        List<String> errors = validationUtil.validateNotNullFields(testObject);

        assertEquals(0, errors.size(), "Expected no validation errors.");
    }

}
