package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMessagingDataMapper {

  default PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(
      OrderCreatedEvent orderCreatedEvent) {
    var order = orderCreatedEvent.order();
    return PaymentRequestAvroModel.newBuilder()
        .setId(UUID.randomUUID())
        .setSagaId(null)
        .setCustomerId(order.getCustomerId().value())
        .setOrderId(order.getId().value())
        .setPrice(order.getPrice().amount())
        .setCreatedAt(orderCreatedEvent.createdAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
        .build();
  }

  default PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(
      OrderCancelledEvent orderCancelledEvent) {
    var order = orderCancelledEvent.order();
    return PaymentRequestAvroModel.newBuilder()
        .setId(UUID.randomUUID())
        .setSagaId(null)
        .setCustomerId(order.getCustomerId().value())
        .setOrderId(order.getId().value())
        .setPrice(order.getPrice().amount())
        .setCreatedAt(orderCancelledEvent.createdAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
        .build();
  }

  default RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(
      OrderPaidEvent orderPaidEvent) {
    var order = orderPaidEvent.order();
    return RestaurantApprovalRequestAvroModel.newBuilder()
        .setId(UUID.randomUUID())
        .setSagaId(null)
        .setOrderId(orderPaidEvent.order().getId().value())
        .setRestaurantId(orderPaidEvent.order().getRestaurantId().value())
        // .setRestaurantOrderStatus(com.food.ordering.system.kafka.order.avro.model.RestaurantOrderStatus.valueOf(order.getOrderStatus().name()))
        .setProducts(
            order.getItems().stream()
                .map(
                    orderItem ->
                        Product.newBuilder()
                            .setId(orderItem.getProduct().getId().value().toString())
                            .setQuantity(orderItem.getQuantity())
                            .build())
                .toList())
        .setPrice(order.getPrice().amount())
        .setCreatedAt(orderPaidEvent.createdAt().toInstant())
        .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
        .build();
  }

  PaymentResponse paymentResponseAvroModelToPaymentResponse(
      PaymentResponseAvroModel paymentResponseAvroModel);

  @Mapping(source = "failureMessages", target = "failureMessage")
  RestaurantApprovalResponse restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
      RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel);
}
