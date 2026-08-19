package com.food.ordering.system.customer.service.domain.outbox.model;

import com.food.ordering.system.outbox.OutboxStatus;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class CustomerOutboxMessage {
  private UUID id;
  private UUID customerId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;
  private String payload;
  @Setter private OutboxStatus outboxStatus;
  private int version;
}
