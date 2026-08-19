package com.food.ordering.system.customer.service.dataaccess.customer.outbox.adapter;

import com.food.ordering.system.customer.service.dataaccess.customer.outbox.mapper.CustomerOutboxDataAccessMapper;
import com.food.ordering.system.customer.service.dataaccess.customer.outbox.repository.CustomerOutboxJpaRepository;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerOutboxRepositoryImpl implements CustomerOutboxRepository {

  private final CustomerOutboxJpaRepository customerOutboxJpaRepository;
  private final CustomerOutboxDataAccessMapper customerOutboxDataAccessMapper;

  @Override
  public CustomerOutboxMessage save(CustomerOutboxMessage customerOutboxMessage) {
    return customerOutboxDataAccessMapper.toCustomerOutboxMessage(
        customerOutboxJpaRepository.save(
            customerOutboxDataAccessMapper.toCustomerOutboxEntity(customerOutboxMessage)));
  }

  @Override
  public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
    customerOutboxJpaRepository.deleteByOutboxStatus(outboxStatus);
  }

  @Override
  public List<CustomerOutboxMessage> findByOutboxStatus(OutboxStatus outboxStatus) {
    return customerOutboxJpaRepository.findByOutboxStatus(outboxStatus).stream()
        .map(customerOutboxDataAccessMapper::toCustomerOutboxMessage)
        .toList();
  }
}
