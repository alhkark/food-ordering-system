package com.food.ordering.system.restaurant.service.domain.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = UtilsMapperCommon.class)
public interface RestaurantDataMapper {

  @Mapping(target = "orderApproval", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(source = "restaurantApprovalRequest", target = "orderDetail")
  Restaurant toRestaurant(RestaurantApprovalRequest restaurantApprovalRequest);

  @Mapping(source = "orderId", target = "orderId")
  @Mapping(source = "restaurantOrderStatus", target = "orderStatus")
  @Mapping(source = "price", target = "totalAmount")
  @Mapping(source = "products", target = "products", qualifiedByName = "toProduct")
  OrderDetail toOrderDetail(RestaurantApprovalRequest restaurantApprovalRequest);

  @Named("toProduct")
  @Mapping(source = "id", target = "productId")
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "price", ignore = true)
  @Mapping(target = "available", ignore = true)
  Product toProduct(Product product);

  default OrderEventPayload toOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
    return OrderEventPayload.builder()
        .orderId(orderApprovalEvent.orderApproval().getOrderId().value().toString())
        .restaurantId(orderApprovalEvent.restaurantId().value().toString())
        .orderApprovalStatus(orderApprovalEvent.orderApproval().getApprovalStatus().name())
        .createdAt(orderApprovalEvent.createdAt())
        .failureMessages(orderApprovalEvent.failureMessages())
        .build();
  }
}
