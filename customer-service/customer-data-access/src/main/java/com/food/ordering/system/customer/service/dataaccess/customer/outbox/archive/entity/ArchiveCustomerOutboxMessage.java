package com.food.ordering.system.customer.service.dataaccess.customer.outbox.archive.entity;

import com.food.ordering.system.outbox.OutboxStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "archive_customer_outbox")
@Entity
public class ArchiveCustomerOutboxMessage {

  @Id private UUID id;
  private String customerId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private String payload;

  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Enumerated(EnumType.STRING)
  private OutboxStatus outboxStatus;

  @Version private int version;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof ArchiveCustomerOutboxMessage that)) return false;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
