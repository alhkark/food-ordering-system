package com.food.ordering.system.payment.service.domain.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface PaymentDataMapper {

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "paymentId", ignore = true)
  @Mapping(target = "paymentStatus", ignore = true)
  Payment toPayment(PaymentRequest paymentRequest);

  default OrderEventPayload toOrderEventPayload(PaymentEvent paymentEvent) {
    return OrderEventPayload.builder()
        .paymentId(paymentEvent.payment().getId().value().toString())
        .customerId(paymentEvent.payment().getCustomerId().value().toString())
        .orderId(paymentEvent.payment().getOrderId().value().toString())
        .price(paymentEvent.payment().getPrice().amount())
        .createdAt(paymentEvent.createdAt())
        .paymentStatus(paymentEvent.payment().getPaymentStatus().name())
        .failureMessages(paymentEvent.failureMessages())
        .build();
  }
}
