package com.example.online_shop_api;

import com.example.online_shop_api.Exceptions.BrandNotExistException;
import com.example.online_shop_api.Exceptions.ColorNotExistException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String DATE_TIME_FORMAT_PATTERN = "HH:mm 'on' dd/MM/yyyy";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String ERROR_KEY = "error";
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErrorsMap = getFieldErrors(ex);
        return createErrorResponse(HttpStatus.BAD_REQUEST, fieldErrorsMap, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = String.format("%s should be of type %s", ex.getName(), getRequiredTypeName(ex));
        return createErrorResponse(HttpStatus.BAD_REQUEST, error, ex);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleBadDataExceptions(RuntimeException ex) {
        String errorMessage = ex.getMessage();
        if (ex instanceof ConstraintViolationException cve) {
            errorMessage =
                    cve.getConstraintViolations().stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining("; "));
        }
        return createErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, ex);
    }

    @ExceptionHandler({
            ColorNotExistException.class,
            BrandNotExistException.class,
    })
    public ResponseEntity<Object> handleEntityConstraintConflict(RuntimeException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex);
    }


    private ResponseEntity<Object> createErrorResponse(
            HttpStatus status, Object error, Exception ex) {
        Map<String, Object> body = new HashMap<>();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
        body.put(TIMESTAMP_KEY, date);
        body.put(ERROR_KEY, error);
        logger.error("Error occurred: {} " , ex.getMessage(), ex);
        return new ResponseEntity<>(body, status);
    }

    private static Map<String, List<String>> getFieldErrors(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        Map<String, List<String>> fieldErrorsMap = new HashMap<>();
        for (FieldError error : fieldErrors) {
            String fieldName = error.getField();
            String errorMessage =
                    error.getDefaultMessage() != null
                            ? error.getDefaultMessage()
                            : "Error message not available";

            if (!fieldErrorsMap.containsKey(fieldName)) {
                fieldErrorsMap.put(fieldName, new ArrayList<>());
            }

            fieldErrorsMap.get(fieldName).add(errorMessage);
        }
        return fieldErrorsMap;
    }

    private static String getRequiredTypeName(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType == null) {
            throw new IllegalStateException("Required type for " + ex.getName() + " was null");
        }
        return requiredType.getName();
    }
}
