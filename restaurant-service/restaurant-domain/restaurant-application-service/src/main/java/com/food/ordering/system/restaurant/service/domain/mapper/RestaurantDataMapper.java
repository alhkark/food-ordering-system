package com.food.ordering.system.restaurant.service.domain.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = UtilsMapperCommon.class)
public interface RestaurantDataMapper {

  @Mapping(target = "orderApproval", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(source = ".", target = "orderDetail")
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
}
