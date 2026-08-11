package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import java.util.List;

public interface ArchivePaymentOutboxRepository {

  List<OrderPaymentOutboxMessage> saveAll(
      List<OrderPaymentOutboxMessage> orderPaymentOutboxMessages);
}
