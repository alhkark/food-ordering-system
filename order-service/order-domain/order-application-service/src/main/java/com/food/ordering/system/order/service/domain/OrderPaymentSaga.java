package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.publisher.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

  private final OrderDomainService orderDomainService;
  private final OrderPaidRestaurantRequestMessagePublisher
      orderPaidRestaurantRequestMessagePublisher;
  private final OrderSagaHelper orderSagaHelper;

  @Override
  @Transactional
  public OrderPaidEvent process(PaymentResponse paymentResponse) {
    log.info("Completing payment for order with id: {}", paymentResponse.orderId());
    var order = orderSagaHelper.findOrder(paymentResponse.orderId());
    var domainEvent =
        orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
    orderSagaHelper.saveOrder(order);
    log.info("Order with id : {} is paid", order.getId().value());
    return domainEvent;
  }

  @Override
  @Transactional
  public EmptyEvent rollback(PaymentResponse paymentResponse) {
    log.info("Canceling order with id: {}", paymentResponse.orderId());
    var order = orderSagaHelper.findOrder(paymentResponse.orderId());
    orderDomainService.cancelOrder(order, paymentResponse.failureMessages());
    orderSagaHelper.saveOrder(order);
    log.info("Order with id : {} is cancelled", order.getId().value());
    return EmptyEvent.INSTANCE;
  }
}
