package com.food.ordering.system.payment.service.messaging.listener.kafka.conf;

import com.food.ordering.system.kafka.consumer.KafkaNotRetryableExceptionsProvider;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.exception.PaymentNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaNotRetryableExceptionsProvider
    implements KafkaNotRetryableExceptionsProvider {
  @Override
  public Class<? extends Exception>[] getExceptions() {
    return new Class[] {PaymentNotFoundException.class, PaymentApplicationServiceException.class};
  }
}
