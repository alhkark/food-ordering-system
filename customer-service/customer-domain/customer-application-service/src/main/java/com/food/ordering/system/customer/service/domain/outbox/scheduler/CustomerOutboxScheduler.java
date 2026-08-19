package com.food.ordering.system.customer.service.domain.outbox.scheduler;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerOutboxScheduler implements OutboxScheduler {

  private final CustomerOutboxHelper customerOutboxHelper;
  private final CustomerMessagePublisher customerMessagePublisher;

  @Override
  @Transactional
  @Scheduled(
      fixedDelayString = "${customer-service.outbox-scheduler-fixed-rate}",
      initialDelayString = "${customer-service.outbox-scheduler-initial-delay}")
  public void processOutboxMessage() {
    List<CustomerOutboxMessage> outboxMessages =
        customerOutboxHelper.getCustomerOutboxMessageByOutboxStatus(OutboxStatus.STARTED);
    if (!outboxMessages.isEmpty()) {
      log.info(
          "Received {} OrderPaymentOutboxMessage with ids: {}, sending to message bus!",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(outboxMessage -> outboxMessage.getId().toString())
              .collect(Collectors.joining(",")));
      outboxMessages.forEach(
          outboxMessage -> {
            customerMessagePublisher.publish(outboxMessage, this::updateOutboxStatus);
          });
      log.info("{} CustomerOutboxMessage sent to message bus!", outboxMessages.size());
    }
  }

  private void updateOutboxStatus(
      CustomerOutboxMessage customerOutboxMessage, OutboxStatus outboxStatus) {
    customerOutboxMessage.setOutboxStatus(outboxStatus);
    customerOutboxHelper.save(customerOutboxMessage);
    log.info("CustomerOutboxMessage is updated with outbox status: {}", outboxStatus.name());
  }
}
