package com.food.ordering.system.customer.service.domain.outbox.scheduler;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerOutboxCleanerScheduler implements OutboxScheduler {

  private final CustomerOutboxHelper customerOutboxHelper;

  @Override
  @Transactional
  @Scheduled(cron = "${customer-service.outbox-cleaner-scheduler:@midnight}")
  public void processOutboxMessage() {
    List<CustomerOutboxMessage> outboxMessages =
        customerOutboxHelper.getCustomerOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
    if (!outboxMessages.isEmpty()) {
      log.info("Received {} CustomerOutboxMessage for clean-up!", outboxMessages.size());
      customerOutboxHelper.archiveApprovalOutboxRepository(outboxMessages);
      customerOutboxHelper.deleteByOutboxStatus(OutboxStatus.COMPLETED);
      log.info("Deleted {} CustomerOutboxMessage!", outboxMessages.size());
    }
  }
}
