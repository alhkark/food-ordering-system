package com.food.ordering.system.payment.service.domain.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.Payment;
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
}
