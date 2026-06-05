package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import java.time.ZonedDateTime;

public record PaymentCancelledEvent(
    Payment payment,
    ZonedDateTime createdAt,
    DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher)
    implements PaymentEvent {
  @Override
  public void fire() {
    paymentCancelledEventDomainEventPublisher.publish(this);
  }
}
