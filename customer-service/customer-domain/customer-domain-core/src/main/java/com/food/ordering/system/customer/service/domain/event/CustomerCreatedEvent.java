package com.food.ordering.system.customer.service.domain.event;

import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.domain.event.DomainEvent;
import java.time.ZonedDateTime;

public record CustomerCreatedEvent(Customer customer, ZonedDateTime createdAt)
    implements DomainEvent<Customer> {}
