package com.food.ordering.system.order.service.domain;

import static com.food.ordering.system.domain.DomainConstants.UTC;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderMapper;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import com.food.ordering.system.saga.SagaStep;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

  private final OrderSagaHelper orderSagaHelper;
  private final OrderDomainService orderDomainService;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final ApprovalOutboxHelper approvalOutboxHelper;
  private final OrderMapper orderMapper;

  @Override
  @Transactional
  public void process(RestaurantApprovalResponse restaurantApprovalResponse) {
    Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(restaurantApprovalResponse.sagaId()), SagaStatus.PROCESSING);

    if (orderApprovalOutboxMessageResponse.isEmpty()) {
      log.info(
          "An outbox message with saga id: {} is already processed!",
          restaurantApprovalResponse.sagaId());
      return;
    }

    OrderApprovalOutboxMessage orderApprovalOutboxMessage =
        orderApprovalOutboxMessageResponse.get();

    Order order = approveOrder(restaurantApprovalResponse);

    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

    approvalOutboxHelper.save(
        getUpdatedApprovalOutboxMessage(
            orderApprovalOutboxMessage, order.getOrderStatus(), sagaStatus));

    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(
            restaurantApprovalResponse.sagaId(), order.getOrderStatus(), sagaStatus));

    log.info("Order with id: {} is approved", order.getId().value());
  }

  @Override
  @Transactional
  public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
    Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(restaurantApprovalResponse.sagaId()), SagaStatus.PROCESSING);

    if (orderApprovalOutboxMessageResponse.isEmpty()) {
      log.info(
          "An outbox message with saga id: {} is already roll backed!",
          restaurantApprovalResponse.sagaId());
      return;
    }

    OrderApprovalOutboxMessage orderApprovalOutboxMessage =
        orderApprovalOutboxMessageResponse.get();

    OrderCancelledEvent domainEvent = rollbackOrder(restaurantApprovalResponse);

    SagaStatus sagaStatus =
        orderSagaHelper.orderStatusToSagaStatus(domainEvent.order().getOrderStatus());

    approvalOutboxHelper.save(
        getUpdatedApprovalOutboxMessage(
            orderApprovalOutboxMessage, domainEvent.order().getOrderStatus(), sagaStatus));

    paymentOutboxHelper.savePaymentOutboxMessage(
        orderMapper.toOrderPaymentEventPayload(domainEvent),
        domainEvent.order().getOrderStatus(),
        sagaStatus,
        OutboxStatus.STARTED,
        UUID.fromString(restaurantApprovalResponse.sagaId()));

    log.info("Order with id: {} is cancelling", domainEvent.order().getId().value());
  }

  private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Approving order with id: {}", restaurantApprovalResponse.orderId());
    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.orderId());
    orderDomainService.approveOrder(order);
    orderSagaHelper.saveOrder(order);
    return order;
  }

  private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
      OrderApprovalOutboxMessage orderApprovalOutboxMessage,
      OrderStatus orderStatus,
      SagaStatus sagaStatus) {
    orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    orderApprovalOutboxMessage.setOrderStatus(orderStatus);
    orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
    return orderApprovalOutboxMessage;
  }

  private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
      String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
    Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(sagaId), SagaStatus.PROCESSING);
    if (orderPaymentOutboxMessageResponse.isEmpty()) {
      throw new OrderDomainException(
          "Payment outbox message cannot be found in " + SagaStatus.PROCESSING.name() + " state");
    }
    OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
    orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    orderPaymentOutboxMessage.setOrderStatus(orderStatus);
    orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
    return orderPaymentOutboxMessage;
  }

  private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Cancelling order with id: {}", restaurantApprovalResponse.orderId());
    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.orderId());
    OrderCancelledEvent domainEvent =
        orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.failureMessage());
    orderSagaHelper.saveOrder(order);
    return domainEvent;
  }
}
