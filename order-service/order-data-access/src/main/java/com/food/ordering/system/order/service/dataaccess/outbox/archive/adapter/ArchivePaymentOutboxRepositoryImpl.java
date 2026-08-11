package com.food.ordering.system.order.service.dataaccess.outbox.archive.adapter;

import com.food.ordering.system.dataaccess.archive.OutboxArchiveBatchPersister;
import com.food.ordering.system.order.service.dataaccess.outbox.archive.mapper.ArchivePaymentOutboxDataAccessMapper;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.ArchivePaymentOutboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchivePaymentOutboxRepositoryImpl implements ArchivePaymentOutboxRepository {

  private final OutboxArchiveBatchPersister outboxArchiveBatchPersister;
  private final ArchivePaymentOutboxDataAccessMapper archivePaymentOutboxDataAccessMapper;

  @Override
  public List<OrderPaymentOutboxMessage> saveAll(
      List<OrderPaymentOutboxMessage> orderPaymentOutboxMessages) {
    var archiveEntities =
        orderPaymentOutboxMessages.stream()
            .map(archivePaymentOutboxDataAccessMapper::toArchiveEntity)
            .toList();
    outboxArchiveBatchPersister.persistAll(archiveEntities);
    return orderPaymentOutboxMessages;
  }
}
