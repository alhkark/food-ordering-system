package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.mapper.OrderMapper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderCreateCommandHandler {

  private final OrderCreateHelper orderCreateHelper;
  private final OrderMapper orderMapper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final OrderSagaHelper orderSagaHelper;

  @Transactional
  public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
    OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
    log.info("Order is created with id: {}", orderCreatedEvent.order().getId().value());
    CreateOrderResponse createOrderResponse =
        orderMapper.toCreateOrderResponse(orderCreatedEvent.order(), "Order created successfully");

    paymentOutboxHelper.savePaymentOutboxMessage(
        orderMapper.toOrderPaymentEventPayload(orderCreatedEvent),
        orderCreatedEvent.order().getOrderStatus(),
        orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.order().getOrderStatus()),
        OutboxStatus.STARTED,
        UUID.randomUUID());

    log.info("Returning CreateOrderResponse with order id: {}", orderCreatedEvent.order().getId());

    return createOrderResponse;
  }
}
