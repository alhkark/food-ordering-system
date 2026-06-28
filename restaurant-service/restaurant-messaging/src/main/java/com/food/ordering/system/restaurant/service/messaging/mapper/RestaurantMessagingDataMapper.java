package com.food.ordering.system.restaurant.service.messaging.mapper;

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UtilsMapperCommon.class)
public interface RestaurantMessagingDataMapper {

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", expression = "java(null)")
  @Mapping(target = "orderId", source = "event.orderApproval.orderId.value")
  @Mapping(target = "restaurantId", source = "event.restaurantId.value")
  @Mapping(target = "orderApprovalStatus", source = "event.orderApproval.approvalStatus")
  RestaurantApprovalResponseAvroModel orderApprovedEventToRestaurantApprovalResponseAvroModel(
      OrderApprovedEvent event);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", expression = "java(null)")
  @Mapping(target = "orderId", source = "event.orderApproval.orderId.value")
  @Mapping(target = "restaurantId", source = "event.restaurantId.value")
  @Mapping(target = "orderApprovalStatus", source = "event.orderApproval.approvalStatus")
  RestaurantApprovalResponseAvroModel orderRejectedEventToRestaurantApprovalResponseAvroModel(
      OrderRejectedEvent event);

  @Mapping(
      target = "restaurantOrderStatus",
      expression = "java(toDomainRestaurantOrderStatus(avroModel))")
  @Mapping(target = "products", source = "products")
  RestaurantApprovalRequest restaurantApprovalRequestAvroModelToRestaurantApproval(
      RestaurantApprovalRequestAvroModel avroModel);

  @Mapping(
      target = "productId",
      expression =
          "java(new com.food.ordering.system.domain.valueobject.ProductId(java.util.UUID.fromString(avroProduct.getId())))")
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "price", ignore = true)
  @Mapping(target = "available", ignore = true)
  Product toProduct(com.food.ordering.system.kafka.order.avro.model.Product avroProduct);

  default com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus
      toAvroOrderApprovalStatus(OrderApprovalStatus orderApproval) {
    return com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus.valueOf(
        orderApproval.name());
  }

  default RestaurantOrderStatus toDomainRestaurantOrderStatus(
      RestaurantApprovalRequestAvroModel avroModel) {
    return RestaurantOrderStatus.valueOf(avroModel.getRestaurantOrderStatus().name());
  }
}
