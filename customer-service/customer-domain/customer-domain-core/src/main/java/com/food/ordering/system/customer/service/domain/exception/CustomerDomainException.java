package com.food.ordering.system.customer.service.domain.exception;

import com.food.ordering.system.domain.exception.DomainException;

public class CustomerDomainException extends DomainException {

  public CustomerDomainException(String message) {
    super(message);
  }

  public CustomerDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
