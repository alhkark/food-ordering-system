package com.food.ordering.system.application.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ProblemDetail handleException(Exception exception) {
    log.error(exception.getMessage(), exception);
    return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error!");
  }

  @ExceptionHandler(value = ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleException(ValidationException validationException) {
    ProblemDetail problemDetail;
    if (validationException instanceof ConstraintViolationException constraintViolationException) {
      String violations = extractViolationFromException(constraintViolationException);
      log.error(violations, validationException);
      problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, violations);
    } else {
      var exceptionMessage = validationException.getMessage();
      log.error(exceptionMessage, validationException);
      problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exceptionMessage);
    }
    return problemDetail;
  }

  private String extractViolationFromException(ConstraintViolationException validationException) {
    return validationException.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining("--"));
  }
}
