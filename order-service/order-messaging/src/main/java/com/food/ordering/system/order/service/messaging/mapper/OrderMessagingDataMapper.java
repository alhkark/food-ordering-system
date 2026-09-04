package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.domain.dto.message.CustomerModel;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.outbox.payload.OrderApprovalEventPayload;
import com.food.ordering.system.outbox.payload.OrderPaymentEventPayload;
import com.food.ordering.system.outbox.payload.PaymentOrderEventPayload;
import com.food.ordering.system.outbox.payload.RestaurantOrderEventPayload;
import debezium_payment_order_outbox.payment.order_outbox.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface OrderMessagingDataMapper {

  @Mapping(target = "id", expression = "java(orderItem.getProduct().getId().value().toString())")
  @Mapping(target = "quantity", source = "quantity")
  com.food.ordering.system.kafka.order.avro.model.Product orderItemToAvroProduct(
      OrderItem orderItem);

  PaymentResponse paymentResponseAvroModelToPaymentResponse(
      PaymentResponseAvroModel paymentResponseAvroModel);

  @Mapping(source = "failureMessages", target = "failureMessage")
  RestaurantApprovalResponse restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
      RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", source = "sagaId")
  PaymentRequestAvroModel toPaymentRequestAvroModel(
      OrderPaymentEventPayload orderPaymentEventPayload, String sagaId);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", source = "sagaId")
  RestaurantApprovalRequestAvroModel toRestaurantApprovalRequestAvroModel(
      OrderApprovalEventPayload orderApprovalEventPayload, String sagaId);

  @Mapping(source = "customerId", target = "id")
  CustomerModel toCustomerModel(CustomerAvroModel customerAvroModel);

  @Mapping(target = "createdAt", source = "paymentResponseAvroModel.createdAt")
  @Mapping(target = "paymentStatus", source = "paymentResponseAvroModel.paymentStatus")
  PaymentResponse toPaymentResponse(
      PaymentOrderEventPayload paymentOrderEventPayload, Value paymentResponseAvroModel);

  @Mapping(target = "createdAt", source = "restaurantApprovalResponseAvroModel.createdAt")
  @Mapping(target = "failureMessage", source = "restaurantOrderEventPayload.failureMessages")
  RestaurantApprovalResponse toApprovalResponse(
      RestaurantOrderEventPayload restaurantOrderEventPayload,
      debezium_restaurant_order_outbox.restaurant.order_outbox.Value
          restaurantApprovalResponseAvroModel);
}
