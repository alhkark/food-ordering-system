package com.food.ordering.system.payment.service.dataaccess.outbox.archive.entity;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
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
@Table(name = "archive_order_outbox")
@Entity
public class ArchiveOrderOutboxEntity {
  @Id private UUID id;
  private UUID sagaId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;
  private String type;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private String payload;

  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Enumerated(EnumType.STRING)
  private OutboxStatus outboxStatus;

  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  @Version private int version;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof ArchiveOrderOutboxEntity that)) return false;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
