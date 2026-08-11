package com.food.ordering.system.order.service.dataaccess.outbox.payment.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface PaymentOutboxDataAccessMapper {

  PaymentOutboxEntity toOutboxEntity(OrderPaymentOutboxMessage orderPaymentOutboxMessage);

  OrderPaymentOutboxMessage toOrderPaymentOutboxMessage(PaymentOutboxEntity paymentOutboxEntity);
}
