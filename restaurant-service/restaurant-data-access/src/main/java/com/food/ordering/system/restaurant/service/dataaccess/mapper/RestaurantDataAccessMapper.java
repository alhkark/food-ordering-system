package com.food.ordering.system.restaurant.service.dataaccess.mapper;

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.restaurant.service.dataaccess.entity.OrderApprovalEntity;
import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface RestaurantDataAccessMapper {

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "restaurantId", source = "restaurantId.value")
  @Mapping(target = "orderId", source = "orderId.value")
  @Mapping(target = "status", source = "approvalStatus")
  OrderApprovalEntity toOrderApprovalEntity(OrderApproval orderApproval);

  @Mapping(target = "orderApprovalId", source = "id")
  @Mapping(target = "restaurantId", source = "restaurantId")
  @Mapping(target = "orderId", source = "orderId")
  @Mapping(target = "approvalStatus", source = "status")
  OrderApproval toOrderApproval(OrderApprovalEntity entity);

  @Mapping(source = "productName", target = "name")
  @Mapping(source = "productPrice", target = "price")
  @Mapping(source = "productAvailable", target = "available")
  @Mapping(target = "quantity", ignore = true)
  Product toProduct(RestaurantEntity entity);

  default Restaurant toRestaurant(List<RestaurantEntity> entities) {
    RestaurantEntity first =
        entities.stream()
            .findFirst()
            .orElseThrow(() -> new RestaurantDataAccessException("No restaurants found!"));
    return Restaurant.builder()
        .restaurantId(new RestaurantId(first.getRestaurantId()))
        .active(first.getRestaurantActive())
        .orderDetail(
            OrderDetail.builder().products(entities.stream().map(this::toProduct).toList()).build())
        .build();
  }

  default List<UUID> toRestaurantProducts(Restaurant restaurant) {
    return restaurant.getOrderDetail().getProducts().stream()
        .map(product -> product.getId().value())
        .toList();
  }

  default OrderApprovalId toOrderApprovalId(UUID id) {
    return new OrderApprovalId(id);
  }
}
