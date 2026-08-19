package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
class CustomerApplicationServiceImpl implements CustomerApplicationService {

  private final CustomerCreateCommandHandler customerCreateCommandHandler;

  @Override
  public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
    return customerCreateCommandHandler.createCustomer(createCustomerCommand);
  }
}
