package com.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
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
@Component
@RequiredArgsConstructor
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

  private final ApprovalOutboxHelper approvalOutboxHelper;

  @Override
  @Scheduled(cron = "${order-service.outbox-cleaner-scheduler:@midnight}")
  public void processOutboxMessage() {
    List<OrderApprovalOutboxMessage> outboxMessages =
        approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED,
            SagaStatus.FAILED,
            SagaStatus.COMPENSATED);
    if (!outboxMessages.isEmpty()) {
      log.info(
          "Received {} OrderApprovalOutboxMessage for clean-up. The payloads: {}",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(OrderApprovalOutboxMessage::getPayload)
              .collect(Collectors.joining("\n")));
      approvalOutboxHelper.archiveApprovalOutboxRepository(outboxMessages);
      approvalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);
      log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size());
    }
  }
}
