package com.food.ordering.system.customer.service.domain.ports.output.repository;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.List;

public interface CustomerOutboxRepository {

  CustomerOutboxMessage save(CustomerOutboxMessage customerOutboxMessage);

  List<CustomerOutboxMessage> findByOutboxStatus(OutboxStatus outboxStatus);

  void deleteByOutboxStatus(OutboxStatus outboxStatus);
}
