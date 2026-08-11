package com.food.ordering.system.payment.service.dataaccess.outbox.archive.adapter;

import com.food.ordering.system.dataaccess.archive.OutboxArchiveBatchPersister;
import com.food.ordering.system.payment.service.dataaccess.outbox.archive.mapper.ArchiveOrderOutboxDataAccessMapper;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.ArchiveOrderOutboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchiveOrderOutboxRepositoryImpl implements ArchiveOrderOutboxRepository {

  private final OutboxArchiveBatchPersister outboxArchiveBatchPersister;
  private final ArchiveOrderOutboxDataAccessMapper archiveApprovalOutboxDataAccessMapper;

  @Override
  public List<OrderOutboxMessage> saveAll(List<OrderOutboxMessage> orderApprovalOutboxMessages) {
    var archiveEntities =
        orderApprovalOutboxMessages.stream()
            .map(archiveApprovalOutboxDataAccessMapper::toArchiveOrderOutboxEntity)
            .toList();
    outboxArchiveBatchPersister.persistAll(archiveEntities);
    return orderApprovalOutboxMessages;
  }
}
