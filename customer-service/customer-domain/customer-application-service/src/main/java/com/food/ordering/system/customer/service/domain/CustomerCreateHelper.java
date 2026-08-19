package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCreateHelper {

  private final CustomerDomainService customerDomainService;

  private final CustomerRepository customerRepository;

  private final CustomerDataMapper customerDataMapper;

  @Transactional
  public CustomerCreatedEvent persistCustomer(CreateCustomerCommand createCustomerCommand) {
    Customer customer = customerDataMapper.toCustomer(createCustomerCommand);
    CustomerCreatedEvent customerCreatedEvent =
        customerDomainService.validateAndInitiateCustomer(customer);
    saveCustomer(customer);
    return customerCreatedEvent;
  }

  private Customer saveCustomer(Customer customer) {
    Customer savedCustomer = customerRepository.createCustomer(customer);
    if (savedCustomer == null) {
      log.error("Could not save customer!");
      throw new CustomerDomainException("Could not save customer!");
    }
    log.info("Customer with id: {} is saved", savedCustomer.getId().value());
    return savedCustomer;
  }
}
