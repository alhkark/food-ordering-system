package com.food.ordering.system.order.service.application.exception.handler;

import com.food.ordering.system.application.handler.GlobalExceptionHandler;
import com.food.ordering.system.order.service.ai.exception.AIOrderNoteInterpreterException;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

  @ExceptionHandler(value = OrderDomainException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleException(OrderDomainException orderDomainException) {
    log.error(orderDomainException.getMessage(), orderDomainException);
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, orderDomainException.getMessage());
  }

  @ExceptionHandler(value = OrderNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ProblemDetail handleException(OrderNotFoundException orderNotFoundException) {
    log.error(orderNotFoundException.getMessage(), orderNotFoundException);
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, orderNotFoundException.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(value = {AIOrderNoteInterpreterException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ProblemDetail handleException(
      AIOrderNoteInterpreterException aiOrderInterpreterException) {
    log.error(aiOrderInterpreterException.getMessage(), aiOrderInterpreterException);
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, aiOrderInterpreterException.getMessage());
  }
}
