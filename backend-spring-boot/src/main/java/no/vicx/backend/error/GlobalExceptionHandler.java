package no.vicx.backend.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "validation error",
                request.getServletPath(),
                exception.getBindingResult().getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Unknown error"),
                                (firstMessage, secondMessage) -> firstMessage)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "validation error",
                request.getServletPath(),
                exception.getConstraintViolations().stream()
                        .collect(Collectors.toMap(
                                violation -> StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                                        .reduce((first, second) -> second)
                                        .map(Path.Node::getName)
                                        .orElse("unknown"),
                                ConstraintViolation::getMessage,
                                (firstMessage, secondMessage) -> firstMessage))
        );
    }
}
