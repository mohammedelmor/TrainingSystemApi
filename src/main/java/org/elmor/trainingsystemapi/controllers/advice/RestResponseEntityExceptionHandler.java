package org.elmor.trainingsystemapi.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.elmor.trainingsystemapi.dtos.ErrorDetails;
import org.elmor.trainingsystemapi.exception.KcBaseException;
import org.elmor.trainingsystemapi.exception.KcUserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleException(Exception ex) {
        var errorDetails = new ErrorDetails();
        errorDetails.setMessage("Error happened");
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(KcBaseException.class)
    public ResponseEntity<ErrorDetails> handleKcUserExceptions(RuntimeException ex) {
        var errorDetails = new ErrorDetails();
        errorDetails.setMessage(ex.getMessage());
        errorDetails.setDetailedMessage(
                ex.getCause() != null ? ex.getCause().getMessage() : "No additional details available."
        );

        String pattern = "(?i).*User exists with same (email|username).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(errorDetails.getDetailedMessage());
        if (matcher.matches()) {
            String field = matcher.group(1);
            errorDetails.setDetailedMessage(String.format("This %s is already taken.", field));
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof KcUserNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
