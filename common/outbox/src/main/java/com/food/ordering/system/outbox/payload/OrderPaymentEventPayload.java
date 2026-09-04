package com.food.ordering.system.outbox.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record OrderPaymentEventPayload(
    @JsonProperty String id,
    @JsonProperty String sagaId,
    @JsonProperty String orderId,
    @JsonProperty String customerId,
    @JsonProperty BigDecimal price,
    @JsonProperty ZonedDateTime createdAt,
    @JsonProperty String paymentOrderStatus) {}
