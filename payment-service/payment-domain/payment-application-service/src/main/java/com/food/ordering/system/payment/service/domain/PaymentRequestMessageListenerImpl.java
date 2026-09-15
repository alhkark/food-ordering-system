package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

  private final PaymentRequestHelper paymentRequestHelper;

  private static final int MAX_EXECUTION = 100;

  @Override
  public void completePayment(PaymentRequest paymentRequest) {
    processPayment(paymentRequestHelper::persistPayment, paymentRequest, "completePayment");
  }

  @Override
  public void cancelPayment(PaymentRequest paymentRequest) {
    processPayment(paymentRequestHelper::persistCancelPayment, paymentRequest, "cancelPayment");
  }

  private void processPayment(
      Function<PaymentRequest, Boolean> func, PaymentRequest paymentRequest, String methodName) {
    int execution = 1;
    boolean result;
    do {
      try {
        result = func.apply(paymentRequest);
        execution++;
      } catch (OptimisticLockingFailureException e) {
        log.warn(
            "Caught OptimisticLockingFailureException in {} with message {}! Retry for order id {}!",
            methodName,
            e.getMessage(),
            paymentRequest.getId());
        result = false;
      }
    } while (!result && execution <= MAX_EXECUTION);

    if (!result) {
      throw new PaymentApplicationServiceException("Payment request could not be completed!");
    }
  }
}
