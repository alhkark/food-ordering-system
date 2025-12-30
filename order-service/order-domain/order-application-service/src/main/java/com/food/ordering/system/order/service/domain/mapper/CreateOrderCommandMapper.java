package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import java.math.BigDecimal;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreateOrderCommandMapper {

  @Mapping(source = "restaurantId", target = "id")
  @Mapping(source = "items", target = "products")
  @Mapping(target = "active", ignore = true)
  Restaurant toRestaurant(CreateOrderCommand createOrderCommand);

  @Mapping(source = "address", target = "deliveryAddress")
  @Mapping(target = "orderId", ignore = true)
  @Mapping(target = "trackingId", ignore = true)
  @Mapping(target = "orderStatus", ignore = true)
  @Mapping(target = "failureMessages", ignore = true)
  Order toOrder(CreateOrderCommand createOrderCommand);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  StreetAddress toStreetAddress(OrderAddress address);

  @Mapping(
      target = "product",
      expression =
          "java(new com.food.ordering.system.order.service.domain.entity.Product(new com.food.ordering.system.domain.valueobject.ProductId(item.productId())))")
  @Mapping(target = "orderItemId", ignore = true)
  @Mapping(target = "orderId", ignore = true)
  com.food.ordering.system.order.service.domain.entity.OrderItem toOrderItemEntities(
      com.food.ordering.system.order.service.domain.dto.create.OrderItem item);

  default Money toMoney(BigDecimal amount) {
    return new Money(amount);
  }

  default RestaurantId toRestaurantId(UUID id) {
    return new RestaurantId(id);
  }

  default Product toProduct(OrderItem orderItem) {
    return new Product(new ProductId(orderItem.productId()));
  }

  default CustomerId toCustomerId(UUID id) {
    return new CustomerId(id);
  }
}
