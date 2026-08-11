package com.food.ordering.system.order.service.domain;

import static com.food.ordering.system.domain.DomainConstants.UTC;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
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
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

  private final OrderDomainService orderDomainService;
  private final OrderSagaHelper orderSagaHelper;
  private final PaymentOutboxHelper paymentOutboxHelper;
  private final ApprovalOutboxHelper approvalOutboxHelper;
  private final OrderMapper orderMapper;

  @Override
  @Transactional
  public void process(PaymentResponse paymentResponse) {
    Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(paymentResponse.sagaId()), SagaStatus.STARTED);
    if (orderPaymentOutboxMessageResponse.isEmpty()) {
      log.info(
          "An outbox message with saga id: {} is already processed!", paymentResponse.sagaId());
      return;
    }
    OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
    var domainEvent = completePaymentForOrder(paymentResponse);
    SagaStatus sagaStatus =
        orderSagaHelper.orderStatusToSagaStatus(domainEvent.order().getOrderStatus());
    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(
            orderPaymentOutboxMessage, domainEvent.order().getOrderStatus(), sagaStatus));
    approvalOutboxHelper.saveApprovalOutboxMessage(
        orderMapper.toOrderApprovalEventPayload(domainEvent),
        domainEvent.order().getOrderStatus(),
        sagaStatus,
        OutboxStatus.STARTED,
        UUID.fromString(paymentResponse.sagaId()));
    log.info("Order with id : {} is paid", domainEvent.order().getId().value());
  }

  @Override
  @Transactional
  public void rollback(PaymentResponse paymentResponse) {
    Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(paymentResponse.sagaId()),
            getCurrentSagaStatus(paymentResponse.paymentStatus()));

    if (orderPaymentOutboxMessageResponse.isEmpty()) {
      log.info(
          "An outbox message with saga id: {} is already roll backed!", paymentResponse.sagaId());
      return;
    }

    OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

    Order order = rollbackPaymentForOrder(paymentResponse);

    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

    paymentOutboxHelper.save(
        getUpdatedPaymentOutboxMessage(
            orderPaymentOutboxMessage, order.getOrderStatus(), sagaStatus));

    if (paymentResponse.paymentStatus() == PaymentStatus.CANCELLED) {
      approvalOutboxHelper.save(
          getUpdatedApprovalOutboxMessage(
              paymentResponse.sagaId(), order.getOrderStatus(), sagaStatus));
    }

    log.info("Order with id: {} is cancelled", order.getId().value());
  }

  private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
      OrderPaymentOutboxMessage orderPaymentOutboxMessage,
      OrderStatus orderStatus,
      SagaStatus sagaStatus) {
    orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    orderPaymentOutboxMessage.setOrderStatus(orderStatus);
    orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
    return orderPaymentOutboxMessage;
  }

  private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
    log.info("Completing payment for order with id: {}", paymentResponse.orderId());
    Order order = orderSagaHelper.findOrder(paymentResponse.orderId());
    OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
    orderSagaHelper.saveOrder(order);
    return domainEvent;
  }

  private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
    return switch (paymentStatus) {
      case COMPLETED -> new SagaStatus[] {SagaStatus.STARTED};
      case CANCELLED -> new SagaStatus[] {SagaStatus.PROCESSING};
      case FAILED -> new SagaStatus[] {SagaStatus.STARTED, SagaStatus.PROCESSING};
    };
  }

  private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
    log.info("Cancelling order with id: {}", paymentResponse.orderId());
    Order order = orderSagaHelper.findOrder(paymentResponse.orderId());
    orderDomainService.cancelOrder(order, paymentResponse.failureMessages());
    orderSagaHelper.saveOrder(order);
    return order;
  }

  private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
      String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
    Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
        approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(sagaId), SagaStatus.COMPENSATING);
    if (orderApprovalOutboxMessageResponse.isEmpty()) {
      throw new OrderDomainException(
          "Approval outbox message could not be found in %s status!"
              .formatted(SagaStatus.COMPENSATING.name()));
    }
    OrderApprovalOutboxMessage orderApprovalOutboxMessage =
        orderApprovalOutboxMessageResponse.get();
    orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
    orderApprovalOutboxMessage.setOrderStatus(orderStatus);
    orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
    return orderApprovalOutboxMessage;
  }
}
