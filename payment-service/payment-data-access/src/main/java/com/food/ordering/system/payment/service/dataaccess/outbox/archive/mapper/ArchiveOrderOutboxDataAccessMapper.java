package com.food.ordering.system.payment.service.dataaccess.outbox.archive.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.dataaccess.outbox.archive.entity.ArchiveOrderOutboxEntity;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface ArchiveOrderOutboxDataAccessMapper {

  ArchiveOrderOutboxEntity toArchiveOrderOutboxEntity(OrderOutboxMessage orderOutboxMessage);
}
