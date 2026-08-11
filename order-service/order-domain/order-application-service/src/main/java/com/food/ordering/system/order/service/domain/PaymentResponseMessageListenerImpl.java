package com.food.ordering.system.order.service.domain;

import static com.food.ordering.system.domain.DomainConstants.FAILURE_MESSAGE_DELIMITER;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@RequiredArgsConstructor
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

  private final OrderPaymentSaga orderPaymentSaga;

  @Override
  public void paymentCompleted(PaymentResponse paymentResponse) {
    orderPaymentSaga.process(paymentResponse);
    log.info(
        "Order Payment Saga process operation is completed for order id: {}",
        paymentResponse.orderId());
  }

  @Override
  public void paymentCancelled(PaymentResponse paymentResponse) {
    orderPaymentSaga.rollback(paymentResponse);
    log.info(
        "Order is roll backed for order id: {}  with failure messages: {}",
        paymentResponse.orderId(),
        String.join(FAILURE_MESSAGE_DELIMITER, paymentResponse.failureMessages()));
  }
}
