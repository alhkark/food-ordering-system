package com.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import static com.food.ordering.system.domain.DomainConstants.UTC;
import static com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantDomainException;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.ArchiveOrderOutboxRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxHelper {

  private final OrderOutboxRepository orderOutboxRepository;
  private final JsonMapper jsonMapper;
  private final ArchiveOrderOutboxRepository archiveOrderOutboxRepository;

  @Transactional(readOnly = true)
  public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
      UUID sagaId, OutboxStatus outboxStatus) {
    return orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(
        ORDER_SAGA_NAME, sagaId, outboxStatus);
  }

  @Transactional(readOnly = true)
  public List<OrderOutboxMessage> getOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
    return orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
  }

  @Transactional
  public void deleteOrderOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
    orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
  }

  @Transactional
  public void saveOrderOutboxMessage(
      OrderEventPayload orderEventPayload,
      OrderApprovalStatus approvalStatus,
      OutboxStatus outboxStatus,
      UUID sagaId) {
    save(
        OrderOutboxMessage.builder()
            .id(UUID.randomUUID())
            .sagaId(sagaId)
            .createdAt(orderEventPayload.createdAt())
            .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
            .type(ORDER_SAGA_NAME)
            .payload(createPayload(orderEventPayload))
            .approvalStatus(approvalStatus)
            .outboxStatus(outboxStatus)
            .build());
  }

  @Transactional
  public void updateOutboxStatus(
      OrderOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
    orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
    save(orderPaymentOutboxMessage);
    log.info("Order outbox table status is updated as: {}", outboxStatus.name());
  }

  @Transactional
  public void archiveApprovalOutboxRepository(List<OrderOutboxMessage> orderOutboxMessages) {
    archiveOrderOutboxRepository.saveAll(orderOutboxMessages);
  }

  private void save(OrderOutboxMessage orderPaymentOutboxMessage) {
    OrderOutboxMessage response = orderOutboxRepository.save(orderPaymentOutboxMessage);
    if (response == null) {
      throw new RestaurantDomainException("Could not save OrderOutboxMessage!");
    }
    log.info("OrderOutboxMessage saved with id: {}", orderPaymentOutboxMessage.getId());
  }

  private String createPayload(OrderEventPayload orderEventPayload) {
    try {
      return jsonMapper.writeValueAsString(orderEventPayload);
    } catch (JacksonException e) {
      log.error("Could not create OrderEventPayload json!", e);
      throw new RestaurantDomainException("Could not create OrderEventPayload json!", e);
    }
  }
}
