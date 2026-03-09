package com.food.ordering.system.order.service.dataaccess.order.entity;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntityId implements Serializable {

  private Long id;
  private OrderEntity order;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof OrderItemEntityId that)) return false;

    return id.equals(that.id) && order.equals(that.order);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + order.hashCode();
    return result;
  }
}
