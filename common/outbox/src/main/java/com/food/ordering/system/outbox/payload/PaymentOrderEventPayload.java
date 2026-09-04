package com.food.ordering.system.outbox.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PaymentOrderEventPayload(
    @JsonProperty String paymentId,
    @JsonProperty String customerId,
    @JsonProperty String orderId,
    @JsonProperty BigDecimal price,
    @JsonProperty ZonedDateTime createdAt,
    @JsonProperty String paymentStatus,
    @JsonProperty List<String> failureMessages) {}
