package com.food.ordering.system.customer.service.domain.ports.output.repository;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import java.util.List;

public interface ArchiveCustomerOutboxRepository {
  List<CustomerOutboxMessage> saveAll(List<CustomerOutboxMessage> customerOutboxMessages);
}
