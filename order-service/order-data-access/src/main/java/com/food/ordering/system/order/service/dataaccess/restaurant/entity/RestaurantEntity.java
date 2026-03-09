package com.food.ordering.system.order.service.dataaccess.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RestaurantEntityId.class)
@Table(name = "order_restaurant_m_view", schema = "restaurant")
@Entity
public class RestaurantEntity {

  @Id private UUID restaurantId;
  @Id private UUID productId;

  private String restaurantName;
  private Boolean restaurantActive;
  private String productName;
  private BigDecimal productPrice;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof RestaurantEntity that)) return false;

    return restaurantId.equals(that.restaurantId) && productId.equals(that.productId);
  }

  @Override
  public int hashCode() {
    int result = restaurantId.hashCode();
    result = 31 * result + productId.hashCode();
    return result;
  }
}
