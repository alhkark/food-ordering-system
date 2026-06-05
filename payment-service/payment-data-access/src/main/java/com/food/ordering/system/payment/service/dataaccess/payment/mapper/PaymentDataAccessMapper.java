package com.food.ordering.system.payment.service.dataaccess.payment.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.dataaccess.payment.entity.PaymentEntity;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.valueobject.PaymentId;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface PaymentDataAccessMapper {

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "customerId", source = "customerId.value")
  @Mapping(target = "orderId", source = "orderId.value")
  @Mapping(source = "payment.paymentStatus", target = "status")
  PaymentEntity toPaymentEntity(Payment payment);

  @Mapping(source = "paymentEntity.status", target = "paymentStatus")
  @Mapping(source = "paymentEntity.id", target = "paymentId")
  Payment toPayment(PaymentEntity paymentEntity);

  default PaymentId toPaymentId(UUID id) {
    return new PaymentId(id);
  }
}
