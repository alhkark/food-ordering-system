package com.food.ordering.system.order.service.domain.outbox.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record OrderPaymentEventPayload(
    @JsonProperty String orderId,
    @JsonProperty String customerId,
    @JsonProperty BigDecimal price,
    @JsonProperty ZonedDateTime createdAt,
    @JsonProperty String paymentOrderStatus) {}
