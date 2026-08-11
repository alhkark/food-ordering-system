package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import java.util.List;

public interface ArchiveApprovalOutboxRepository {

  List<OrderApprovalOutboxMessage> saveAll(
      List<OrderApprovalOutboxMessage> orderApprovalOutboxMessages);
}
