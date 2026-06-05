package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface OrderMessagingDataMapper {

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", ignore = true)
  @Mapping(target = "customerId", source = "order.customerId.value")
  @Mapping(target = "orderId", source = "order.id.value")
  @Mapping(target = "price", source = "order.price.amount")
  @Mapping(target = "paymentOrderStatus", constant = "PENDING")
  PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(
      OrderCreatedEvent orderCreatedEvent);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", ignore = true)
  @Mapping(target = "customerId", source = "order.customerId.value")
  @Mapping(target = "orderId", source = "order.id.value")
  @Mapping(target = "price", source = "order.price.amount")
  @Mapping(target = "paymentOrderStatus", constant = "CANCELLED")
  PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(
      OrderCancelledEvent orderCancelledEvent);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", ignore = true)
  @Mapping(target = "orderId", source = "order.id.value")
  @Mapping(target = "restaurantId", source = "order.restaurantId.value")
  @Mapping(target = "products", source = "order.items")
  @Mapping(target = "price", source = "order.price.amount")
  @Mapping(target = "restaurantOrderStatus", constant = "PAID")
  RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(
      OrderPaidEvent orderPaidEvent);

  @Mapping(target = "id", expression = "java(orderItem.getProduct().getId().value().toString())")
  @Mapping(target = "quantity", source = "quantity")
  com.food.ordering.system.kafka.order.avro.model.Product orderItemToAvroProduct(
      OrderItem orderItem);

  PaymentResponse paymentResponseAvroModelToPaymentResponse(
      PaymentResponseAvroModel paymentResponseAvroModel);

  @Mapping(source = "failureMessages", target = "failureMessage")
  RestaurantApprovalResponse restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
      RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel);
}
