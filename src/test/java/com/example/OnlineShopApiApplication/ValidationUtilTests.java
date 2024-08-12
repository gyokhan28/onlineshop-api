package com.example.OnlineShopApiApplication;

import com.example.online_shop_api.Utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationUtilTests {
    @Mock
    private Validator validator;

    @InjectMocks
    private ValidationUtil validationUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidate_noViolations() {
        Object validObject = new Object();
        lenient().when(validator.validate(validObject)).thenReturn(new HashSet<>());

        Map<String, String> result = validationUtil.validate(validObject);

        assertTrue(result.isEmpty(), "Expected no validation errors.");
    }

}
