package com.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxCleanerScheduler implements OutboxScheduler {

  private final OrderOutboxHelper orderOutboxHelper;

  @Transactional
  @Scheduled(cron = "@midnight")
  @Override
  public void processOutboxMessage() {
    List<OrderOutboxMessage> outboxMessages =
        orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
    if (!outboxMessages.isEmpty()) {
      log.info("Received {} OrderOutboxMessage for clean-up!", outboxMessages.size());
      orderOutboxHelper.archiveApprovalOutboxRepository(outboxMessages);
      orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
      log.info("Deleted {} OrderOutboxMessage!", outboxMessages.size());
    }
  }
}
