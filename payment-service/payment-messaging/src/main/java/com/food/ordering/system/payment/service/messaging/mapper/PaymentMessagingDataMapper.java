package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface PaymentMessagingDataMapper {

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", expression = "java(null)")
  @Mapping(target = "paymentId", source = "payment.id.value")
  @Mapping(target = "customerId", source = "payment.customerId.value")
  @Mapping(target = "orderId", source = "payment.orderId.value")
  @Mapping(target = "price", source = "payment.price.amount")
  @Mapping(target = "paymentStatus", source = ".", qualifiedByName = "toPaymentStatus")
  @Mapping(target = "failureMessages", expression = "java(new java.util.ArrayList<>())")
  PaymentResponseAvroModel toPaymentResponseAvroModel(PaymentCompletedEvent paymentCompletedEvent);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", expression = "java(null)")
  @Mapping(target = "paymentId", source = "payment.id.value")
  @Mapping(target = "customerId", source = "payment.customerId.value")
  @Mapping(target = "orderId", source = "payment.orderId.value")
  @Mapping(target = "price", source = "payment.price.amount")
  @Mapping(target = "paymentStatus", source = ".", qualifiedByName = "toPaymentStatus")
  @Mapping(target = "failureMessages", expression = "java(new java.util.ArrayList<>())")
  PaymentResponseAvroModel toPaymentResponseAvroModel(PaymentCancelledEvent paymentCancelledEvent);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "sagaId", expression = "java(null)")
  @Mapping(target = "paymentId", source = "payment.id.value")
  @Mapping(target = "customerId", source = "payment.customerId.value")
  @Mapping(target = "orderId", source = "payment.orderId.value")
  @Mapping(target = "price", source = "payment.price.amount")
  @Mapping(target = "paymentStatus", source = ".", qualifiedByName = "toPaymentStatus")
  PaymentResponseAvroModel toPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEvent);

  PaymentRequest toPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel);

  @Named("toPaymentStatus")
  default PaymentStatus toPaymentStatus(PaymentEvent paymentEvent) {
    return PaymentStatus.valueOf(paymentEvent.payment().getPaymentStatus().name());
  }
}
