package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

  private final PaymentOutboxHelper paymentOutboxHelper;

  @Override
  @Scheduled(cron = "${order-service.outbox-cleaner-scheduler:@midnight}")
  public void processOutboxMessage() {
    List<OrderPaymentOutboxMessage> outboxMessages =
        paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED,
            SagaStatus.FAILED,
            SagaStatus.COMPENSATED);

    if (!outboxMessages.isEmpty()) {
      log.info(
          "Received {} OrderPaymentOutboxMessage for clean-up. The payloads: {}",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(OrderPaymentOutboxMessage::getPayload)
              .collect(Collectors.joining("\n")));
      paymentOutboxHelper.archivePaymentOutboxRepository(outboxMessages);
      paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);
      log.info("{} OrderPaymentOutboxMessage deleted!", outboxMessages.size());
    }
  }
}
