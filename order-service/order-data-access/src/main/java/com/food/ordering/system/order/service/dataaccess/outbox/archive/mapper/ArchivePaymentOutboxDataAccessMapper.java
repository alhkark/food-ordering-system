package com.food.ordering.system.order.service.dataaccess.outbox.archive.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.dataaccess.outbox.archive.entity.ArchivePaymentOutboxEntity;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface ArchivePaymentOutboxDataAccessMapper {

  ArchivePaymentOutboxEntity toArchiveEntity(OrderPaymentOutboxMessage orderPaymentOutboxMessage);
}
