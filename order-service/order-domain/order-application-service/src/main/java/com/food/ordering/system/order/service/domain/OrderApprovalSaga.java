package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.publisher.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderApprovalSaga
    implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

  private final OrderSagaHelper orderSagaHelper;
  private final OrderDomainService orderDomainService;
  private final OrderCancelledPaymentRequestMessagePublisher
      orderCancelledPaymentRequestMessagePublisher;

  @Override
  @Transactional
  public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Approving order with id: {}", restaurantApprovalResponse.orderId());
    var order = orderSagaHelper.findOrder(restaurantApprovalResponse.orderId());
    orderDomainService.approveOrder(order);
    orderSagaHelper.saveOrder(order);
    log.info("Order with id: {} is approved", order.getId().value());
    return EmptyEvent.INSTANCE;
  }

  @Override
  @Transactional
  public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Cancelling order with id: {}", restaurantApprovalResponse.orderId());
    var order = orderSagaHelper.findOrder(restaurantApprovalResponse.orderId());
    var orderCancelledEvent =
        orderDomainService.cancelOrderPayment(
            order,
            restaurantApprovalResponse.failureMessage(),
            orderCancelledPaymentRequestMessagePublisher);
    orderSagaHelper.saveOrder(order);
    log.info("Order with id: {} is cancelled", order.getId().value());
    return orderCancelledEvent;
  }
}
