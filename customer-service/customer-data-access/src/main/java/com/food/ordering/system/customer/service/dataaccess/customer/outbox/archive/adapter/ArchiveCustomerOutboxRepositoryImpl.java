package com.food.ordering.system.customer.service.dataaccess.customer.outbox.archive.adapter;

import com.food.ordering.system.customer.service.dataaccess.customer.outbox.archive.mapper.ArchiveCustomerDataAccessMapper;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.repository.ArchiveCustomerOutboxRepository;
import com.food.ordering.system.dataaccess.archive.OutboxArchiveBatchPersister;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArchiveCustomerOutboxRepositoryImpl implements ArchiveCustomerOutboxRepository {

  private final OutboxArchiveBatchPersister outboxArchiveBatchPersister;
  private final ArchiveCustomerDataAccessMapper archiveCustomerDataAccessMapper;

  @Override
  public List<CustomerOutboxMessage> saveAll(List<CustomerOutboxMessage> customerOutboxMessages) {
    var archiveEntities =
        customerOutboxMessages.stream()
            .map(archiveCustomerDataAccessMapper::toArchiveEntity)
            .toList();
    outboxArchiveBatchPersister.persistAll(archiveEntities);
    return customerOutboxMessages;
  }
}
