package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import java.util.UUID;

public class OrderMessagingDataMapper {

  public PaymentRequestAvroModel toPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
    var order = orderCreatedEvent.order();
    return PaymentRequestAvroModel.newBuilder()
        .setId(UUID.randomUUID().toString())
        .setSagaId("")
        .setCustomerId(order.getCustomerId().value().toString())
        .setOrderId(order.getId().value().toString())
        .setPrice(order.getPrice().amount())
        .setCreatedAt(orderCreatedEvent.createdAt().toInstant())
        .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
        .build();
  }
}
