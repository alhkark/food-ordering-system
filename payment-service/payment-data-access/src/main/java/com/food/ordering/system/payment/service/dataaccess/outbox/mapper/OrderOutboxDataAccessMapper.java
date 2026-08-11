package com.food.ordering.system.payment.service.dataaccess.outbox.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.dataaccess.outbox.entity.OrderOutboxEntity;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface OrderOutboxDataAccessMapper {

  OrderOutboxEntity toOutboxEntity(OrderOutboxMessage orderOutboxMessage);

  OrderOutboxMessage toOrderOutboxMessage(OrderOutboxEntity paymentOutboxEntity);
}
