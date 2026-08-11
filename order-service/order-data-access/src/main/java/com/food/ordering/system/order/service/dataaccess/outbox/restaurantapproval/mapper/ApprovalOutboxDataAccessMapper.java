package com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface ApprovalOutboxDataAccessMapper {

  ApprovalOutboxEntity orderCreatedOutboxMessageToOutboxEntity(
      OrderApprovalOutboxMessage orderApprovalOutboxMessage);

  OrderApprovalOutboxMessage approvalOutboxEntityToOrderApprovalOutboxMessage(
      ApprovalOutboxEntity approvalOutboxEntity);
}
