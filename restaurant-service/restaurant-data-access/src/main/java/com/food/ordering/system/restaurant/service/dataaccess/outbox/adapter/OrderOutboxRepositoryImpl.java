package com.food.ordering.system.restaurant.service.dataaccess.outbox.adapter;

import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.restaurant.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
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
  public OrderOutboxMessage save(OrderOutboxMessage orderApprovalOutboxMessage) {
    return orderOutboxDataAccessMapper.toOrderOutboxMessage(
        orderOutboxJpaRepository.save(
            orderOutboxDataAccessMapper.toOutboxEntity(orderApprovalOutboxMessage)));
  }

  @Override
  public List<OrderOutboxMessage> findByTypeAndOutboxStatus(
      String sagaType, OutboxStatus outboxStatus) {
    return orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus).stream()
        .map(orderOutboxDataAccessMapper::toOrderOutboxMessage)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(
      String type, UUID sagaId, OutboxStatus outboxStatus) {
    return orderOutboxJpaRepository
        .findByTypeAndSagaIdAndOutboxStatus(type, sagaId, outboxStatus)
        .map(orderOutboxDataAccessMapper::toOrderOutboxMessage);
  }

  @Override
  public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
    orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
  }
}
