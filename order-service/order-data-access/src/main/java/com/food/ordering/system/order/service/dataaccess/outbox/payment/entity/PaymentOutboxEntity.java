package com.food.ordering.system.order.service.dataaccess.outbox.payment.entity;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
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
@Table(name = "payment_outbox")
@Entity
public class PaymentOutboxEntity {

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
  private SagaStatus sagaStatus;

  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Enumerated(EnumType.STRING)
  private OutboxStatus outboxStatus;

  @Version private int version;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof PaymentOutboxEntity that)) return false;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
