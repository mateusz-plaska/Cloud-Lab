package org.pwr.cloud.lab.common.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@RequiredArgsConstructor
public class OutboundExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(
            value = {
                OrderNotFoundException.class,
                PackingTaskNotFoundException.class,
                BoxSizeNotFoundException.class,
                PickingTaskNotFoundException.class
            })
    public ErrorResponseDto handleNotFoundException(DomainRuntimeException ex) {
        return new ErrorResponseDto(Instant.now(), ex.getMessage(), NOT_FOUND.value(), ex.getCode(), ex.getArgs());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> args = ex.getBindingResult().getFieldErrors().stream()
                .filter(fieldError -> Objects.nonNull(fieldError.getDefaultMessage()))
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (v1, v2) -> v1));

        var attributesValidationException = new AttributesValidationException(args);

        return new ErrorResponseDto(
                Instant.now(),
                attributesValidationException.getMessage(),
                BAD_REQUEST.value(),
                attributesValidationException.getCode(),
                attributesValidationException.getArgs());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ErrorResponseDto handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> args = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            var parameterName = StreamSupport.stream(cv.getPropertyPath().spliterator(), false)
                    .filter(e -> e.getKind() == ElementKind.PARAMETER || e.getKind() == ElementKind.PROPERTY)
                    .map(Path.Node::toString)
                    .collect(Collectors.joining("."));
            if (!parameterName.isEmpty()) {
                args.put(parameterName, cv.getMessage());
            }
        });

        var attributesValidationException = new AttributesValidationException(args);

        return new ErrorResponseDto(
                Instant.now(),
                attributesValidationException.getMessage(),
                BAD_REQUEST.value(),
                attributesValidationException.getCode(),
                attributesValidationException.getArgs());
    }

    public record ErrorResponseDto(
            Instant timestamp, String message, int httpStatus, String code, Map<String, Object> args) {}
}
