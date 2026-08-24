package com.food.ordering.system.order.service.dataaccess.order.entity;

import com.food.ordering.system.domain.valueobject.OrderPreferences;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Entity
public class OrderEntity {

  @Id private UUID id;
  private UUID customerId;
  private UUID restaurantId;
  private UUID trackingId;
  private BigDecimal price;

  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @JdbcTypeCode(SqlTypes.JSON)
  // @Column(name = "order_preferences")
  private OrderPreferences orderPreferences;

  private String failureMessages;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
  private OrderAddressEntity address;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItemEntity> items;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof OrderEntity that)) return false;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
