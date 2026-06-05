package com.food.ordering.system.dataaccess.restaurant.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntityId implements Serializable {

  private UUID restaurantId;
  private UUID productId;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof RestaurantEntityId that)) return false;

    return restaurantId.equals(that.restaurantId) && productId.equals(that.productId);
  }

  @Override
  public int hashCode() {
    int result = restaurantId.hashCode();
    result = 31 * result + productId.hashCode();
    return result;
  }
}
