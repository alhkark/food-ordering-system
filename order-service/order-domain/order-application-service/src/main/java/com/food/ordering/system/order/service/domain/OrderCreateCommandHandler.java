package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.mapper.OrderMapper;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreateCommandHandler {

  private final OrderCreateHelper orderCreateHelper;

  private final OrderMapper orderMapper;

  private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

  public OrderCreateCommandHandler(
      OrderCreateHelper orderCreateHelper,
      OrderMapper orderMapper,
      OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher) {
    this.orderCreateHelper = orderCreateHelper;
    this.orderMapper = orderMapper;
    this.orderCreatedPaymentRequestMessagePublisher = orderCreatedPaymentRequestMessagePublisher;
  }

  public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
    var orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
    log.info("Order created with id: {}", orderCreatedEvent.order().getId());
    orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
    return orderMapper.toCreateOrderResponse(orderCreatedEvent.order());
  }
}
