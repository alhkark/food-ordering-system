package com.food.ordering.system.payment.service.dataaccess.outbox.adapter;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper;
import com.food.ordering.system.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

  private final OrderOutboxJpaRepository orderOutboxJpaRepository;
  private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

  @Override
  public OrderOutboxMessage save(OrderOutboxMessage orderPaymentOutboxMessage) {
    return orderOutboxDataAccessMapper.toOrderOutboxMessage(
        orderOutboxJpaRepository.save(
            orderOutboxDataAccessMapper.toOutboxEntity(orderPaymentOutboxMessage)));
  }

  @Override
  public List<OrderOutboxMessage> findByTypeAndOutboxStatus(
      String sagaType, OutboxStatus outboxStatus) {
    return orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus).stream()
        .map(orderOutboxDataAccessMapper::toOrderOutboxMessage)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
      String sagaType, UUID sagaId, PaymentStatus paymentStatus, OutboxStatus outboxStatus) {
    return orderOutboxJpaRepository
        .findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            sagaType, sagaId, paymentStatus, outboxStatus)
        .map(orderOutboxDataAccessMapper::toOrderOutboxMessage);
  }

  @Override
  public void deleteByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
    orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(sagaType, outboxStatus);
  }
}
