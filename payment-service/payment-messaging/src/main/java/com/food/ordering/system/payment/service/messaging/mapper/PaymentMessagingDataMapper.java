package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
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
}
