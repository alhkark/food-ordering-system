package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapper.class, UtilsMapperCommon.class})
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
  @Mapping(target = "orderPreferences", ignore = true)
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

  default Product toProduct(OrderItem orderItem) {
    return new Product(new ProductId(orderItem.productId()));
  }
}
