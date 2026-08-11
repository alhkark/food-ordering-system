package com.food.ordering.system.order.service.dataaccess.outbox.payment.adapter;

import com.food.ordering.system.order.service.dataaccess.outbox.payment.mapper.PaymentOutboxDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

  private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
  private final PaymentOutboxDataAccessMapper paymentOutboxDataAccessMapper;

  @Override
  public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
    return paymentOutboxDataAccessMapper.toOrderPaymentOutboxMessage(
        paymentOutboxJpaRepository.save(
            paymentOutboxDataAccessMapper.toOutboxEntity(orderPaymentOutboxMessage)));
  }

  @Override
  public List<OrderPaymentOutboxMessage> findByTypeAndOutboxStatusAndSagaStatus(
      String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    return paymentOutboxJpaRepository
        .findByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, Arrays.asList(sagaStatus))
        .stream()
        .map(paymentOutboxDataAccessMapper::toOrderPaymentOutboxMessage)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
      String type, UUID sagaId, SagaStatus... sagaStatus) {
    return paymentOutboxJpaRepository
        .findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, Arrays.asList(sagaStatus))
        .map(paymentOutboxDataAccessMapper::toOrderPaymentOutboxMessage);
  }

  @Override
  public void deleteByTypeAndOutboxStatusAndSagaStatus(
      String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type, outboxStatus, Arrays.asList(sagaStatus));
  }
}
