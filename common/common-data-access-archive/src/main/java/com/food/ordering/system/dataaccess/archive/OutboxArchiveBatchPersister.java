package com.food.ordering.system.dataaccess.archive;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxArchiveBatchPersister {

  private final EntityManager entityManager;

  @Value("${archive.jdbc-batch-size:30}")
  private int batchSize;

  public <T> void persistAll(List<T> entities) {
    if (entities.isEmpty()) {
      return;
    }

    Session session = entityManager.unwrap(Session.class);
    session.setJdbcBatchSize(batchSize);

    for (int i = 0; i < entities.size(); i++) {
      entityManager.persist(entities.get(i));
      if (i > 0 && i % batchSize == 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }
  }
}
