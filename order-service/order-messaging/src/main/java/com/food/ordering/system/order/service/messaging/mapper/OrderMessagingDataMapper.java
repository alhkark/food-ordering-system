package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
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
}
