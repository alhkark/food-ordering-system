package com.food.ordering.system.customer.service.domain.ports.output.message.publisher;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.function.BiConsumer;

public interface CustomerMessagePublisher {

  void publish(
      CustomerOutboxMessage customerOutboxMessage,
      BiConsumer<CustomerOutboxMessage, OutboxStatus> outboxCallback);
}
