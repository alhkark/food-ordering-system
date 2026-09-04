package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.outbox.payload.OrderPaymentEventPayload;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import debezium_order_payment_outbox.order.payment_outbox.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface PaymentMessagingDataMapper {

  PaymentRequest toPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel);

  @Named("toPaymentStatus")
  default PaymentStatus toPaymentStatus(PaymentEvent paymentEvent) {
    return PaymentStatus.valueOf(paymentEvent.payment().getPaymentStatus().name());
  }

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", source = "sagaId")
  PaymentResponseAvroModel toPaymentResponseAvroModel(
      OrderEventPayload orderEventPayload, String sagaId);

  @Mapping(target = "id", source = "paymentRequestAvroModel.id")
  @Mapping(target = "sagaId", source = "paymentRequestAvroModel.sagaId")
  @Mapping(target = "createdAt", source = "paymentRequestAvroModel.createdAt")
  PaymentRequest toPaymentRequest(
      OrderPaymentEventPayload orderPaymentEventPayload, Value paymentRequestAvroModel);
}
