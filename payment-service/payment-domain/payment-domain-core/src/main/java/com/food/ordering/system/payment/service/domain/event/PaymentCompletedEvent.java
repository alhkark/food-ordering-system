package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.payment.service.domain.entity.Payment;
import java.time.ZonedDateTime;

public record PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt)
    implements PaymentEvent {}
