package com.food.ordering.system.order.service.dataaccess.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OrderItemEntityId.class)
@Table(name = "order_items")
@Entity
public class OrderItemEntity {
  @Id private Long id;

  @Id
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "ORDER_ID")
  private OrderEntity order;

  private UUID productId;
  private BigDecimal price;
  private Integer quantity;
  private BigDecimal subTotal;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof OrderItemEntity that)) return false;

    return id.equals(that.id) && order.equals(that.order);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + order.hashCode();
    return result;
  }
}
