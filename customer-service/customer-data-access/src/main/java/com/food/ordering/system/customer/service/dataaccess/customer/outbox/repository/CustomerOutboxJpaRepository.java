package com.food.ordering.system.customer.service.dataaccess.customer.outbox.repository;

import com.food.ordering.system.customer.service.dataaccess.customer.outbox.entity.CustomerOutboxEntity;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOutboxJpaRepository extends JpaRepository<CustomerOutboxEntity, UUID> {

  List<CustomerOutboxEntity> findByOutboxStatus(OutboxStatus outboxStatus);

  void deleteByOutboxStatus(OutboxStatus outboxStatus);
}
