package com.food.ordering.system.mapper;

import com.food.ordering.system.domain.valueobject.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UtilsMapperCommon {

  default Money toMoney(BigDecimal amount) {
    return new Money(amount);
  }

  default BigDecimal toBigDecimal(Money money) {
    return money.amount();
  }

  default CustomerId toCustomerId(UUID id) {
    return new CustomerId(id);
  }

  default ProductId toProductId(UUID id) {
    return new ProductId(id);
  }

  default RestaurantId toRestaurantId(UUID id) {
    return new RestaurantId(id);
  }

  default RestaurantId toRestaurantId(String restaurantId) {
    return toRestaurantId(UUID.fromString(restaurantId));
  }

  default OrderId toOrderId(UUID id) {
    return new OrderId(id);
  }

  default OrderId toOrderId(String orderId) {
    return toOrderId(UUID.fromString(orderId));
  }

  default Instant toInstant(ZonedDateTime createdAt) {
    return createdAt.toInstant();
  }

  default OrderStatus toOrderStatus(RestaurantOrderStatus restaurantOrderStatus) {
    return OrderStatus.valueOf(restaurantOrderStatus.name());
  }
}
