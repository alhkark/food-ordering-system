package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import java.math.BigDecimal;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UtilsMapper {

  default Money toMoney(BigDecimal amount) {
    return new Money(amount);
  }

  default BigDecimal toBigDecimal(Money money) {
    return money.amount();
  }

  default RestaurantId toRestaurantId(UUID id) {
    return new RestaurantId(id);
  }

  default CustomerId toCustomerId(UUID id) {
    return new CustomerId(id);
  }

  default UUID toOrderTrackingId(TrackingId trackingId) {
    return trackingId.trackingId();
  }

  default TrackingId toTrackingId(UUID uuid) {
    return new TrackingId(uuid);
  }

  default Product toProduct(UUID uuid) {
    var productId = new ProductId(uuid);
    return new Product(productId);
  }

  default OrderId toOrderId(UUID uuid) {
    return new OrderId(uuid);
  }

  default OrderItemId toOrderItemId(Long id) {
    return new OrderItemId(id);
  }

  default ProductId toProductId(UUID uuid) {
    return new ProductId(uuid);
  }
}
