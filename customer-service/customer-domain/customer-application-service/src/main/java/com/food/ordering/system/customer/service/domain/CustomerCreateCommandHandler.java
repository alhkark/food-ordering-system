package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.food.ordering.system.customer.service.domain.outbox.scheduler.CustomerOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
class CustomerCreateCommandHandler {

  private final CustomerDataMapper customerDataMapper;

  private final CustomerCreateHelper customerCreateHelper;

  private final CustomerOutboxHelper customerOutboxHelper;

  @Transactional
  public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
    CustomerCreatedEvent customerCreatedEvent =
        customerCreateHelper.persistCustomer(createCustomerCommand);
    CreateCustomerResponse createCustomerResponse =
        customerDataMapper.toCreateCustomerResponse(
            customerCreatedEvent.customer(), "Customer created successfully!");

    customerOutboxHelper.saveCustomerOutboxMessage(
        customerDataMapper.toCustomerEventPayload(customerCreatedEvent),
        OutboxStatus.STARTED,
        customerCreatedEvent.createdAt());

    log.info(
        "Returning CreateCustomerResponse for customer id: {}", createCustomerCommand.customerId());
    return createCustomerResponse;
  }
}
