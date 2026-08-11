package com.food.ordering.system.order.service.dataaccess.outbox.archive.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.dataaccess.outbox.archive.entity.ArchiveApprovalOutboxEntity;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface ArchiveApprovalOutboxDataAccessMapper {

  ArchiveApprovalOutboxEntity toArchiveEntity(
      OrderApprovalOutboxMessage orderApprovalOutboxMessage);
}
