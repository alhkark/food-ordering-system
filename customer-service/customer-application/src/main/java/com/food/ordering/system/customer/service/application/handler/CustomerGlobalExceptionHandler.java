package com.food.ordering.system.customer.service.application.handler;

import com.food.ordering.system.application.handler.GlobalExceptionHandler;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CustomerGlobalExceptionHandler extends GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(value = {CustomerDomainException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleException(CustomerDomainException exception) {
    log.error(exception.getMessage(), exception);
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
  }
}
