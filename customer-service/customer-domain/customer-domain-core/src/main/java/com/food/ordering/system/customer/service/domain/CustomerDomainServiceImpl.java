package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.annotation.DomainService;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainService
public class CustomerDomainServiceImpl implements CustomerDomainService {

  public CustomerCreatedEvent validateAndInitiateCustomer(Customer customer) {
    log.info("Customer with id: {} is initiated", customer.getId().value());
    return new CustomerCreatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
  }
}
