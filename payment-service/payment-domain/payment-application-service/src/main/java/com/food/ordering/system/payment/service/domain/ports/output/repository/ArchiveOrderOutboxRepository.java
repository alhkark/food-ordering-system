package com.food.ordering.system.payment.service.domain.ports.output.repository;

import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import java.util.List;

public interface ArchiveOrderOutboxRepository {

  List<OrderOutboxMessage> saveAll(List<OrderOutboxMessage> orderOutboxMessage);
}
