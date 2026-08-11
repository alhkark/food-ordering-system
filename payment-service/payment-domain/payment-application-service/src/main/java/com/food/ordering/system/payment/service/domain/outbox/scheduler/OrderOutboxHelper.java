package com.food.ordering.system.payment.service.domain.outbox.scheduler;

import static com.food.ordering.system.domain.DomainConstants.UTC;
import static com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.exception.PaymentDomainException;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.ArchiveOrderOutboxRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxRepository;
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
  private final ArchiveOrderOutboxRepository archiveOrderOutboxRepository;
  private final JsonMapper jsonMapper;

  @Transactional(readOnly = true)
  public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
      UUID sagaId, PaymentStatus paymentStatus) {
    return orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        ORDER_SAGA_NAME, sagaId, paymentStatus, OutboxStatus.COMPLETED);
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
      PaymentStatus paymentStatus,
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
            .paymentStatus(paymentStatus)
            .outboxStatus(outboxStatus)
            .build());
  }

  @Transactional
  public void updateOutboxMessage(
      OrderOutboxMessage orderOutboxMessage, OutboxStatus outboxStatus) {
    orderOutboxMessage.setOutboxStatus(outboxStatus);
    save(orderOutboxMessage);
    log.info("Order outbox table status is updated as: {}", outboxStatus.name());
  }

  @Transactional
  public void archiveApprovalOutboxRepository(List<OrderOutboxMessage> orderOutboxMessages) {
    archiveOrderOutboxRepository.saveAll(orderOutboxMessages);
  }

  private String createPayload(OrderEventPayload orderEventPayload) {
    try {
      return jsonMapper.writeValueAsString(orderEventPayload);
    } catch (JacksonException e) {
      log.error("Could not create OrderEventPayload json!", e);
      throw new PaymentDomainException("Could not create OrderEventPayload json!", e);
    }
  }

  private void save(OrderOutboxMessage orderOutboxMessage) {
    OrderOutboxMessage response = orderOutboxRepository.save(orderOutboxMessage);
    if (response == null) {
      log.error("Could not save OrderOutboxMessage!");
      throw new PaymentDomainException("Could not save OrderOutboxMessage!");
    }
    log.info("OrderOutboxMessage is saved with id: {}", orderOutboxMessage.getId());
  }
}
