package com.food.ordering.system.order.service.dataaccess.outbox.archive.adapter;

import com.food.ordering.system.dataaccess.archive.OutboxArchiveBatchPersister;
import com.food.ordering.system.order.service.dataaccess.outbox.archive.mapper.ArchiveApprovalOutboxDataAccessMapper;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.ArchiveApprovalOutboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchiveApprovalOutboxRepositoryImpl implements ArchiveApprovalOutboxRepository {

  private final OutboxArchiveBatchPersister outboxArchiveBatchPersister;
  private final ArchiveApprovalOutboxDataAccessMapper archiveApprovalOutboxDataAccessMapper;

  @Override
  public List<OrderApprovalOutboxMessage> saveAll(
      List<OrderApprovalOutboxMessage> orderApprovalOutboxMessages) {
    var archiveEntities =
        orderApprovalOutboxMessages.stream()
            .map(archiveApprovalOutboxDataAccessMapper::toArchiveEntity)
            .toList();
    outboxArchiveBatchPersister.persistAll(archiveEntities);
    return orderApprovalOutboxMessages;
  }
}
