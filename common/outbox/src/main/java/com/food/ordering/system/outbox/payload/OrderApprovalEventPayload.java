package com.food.ordering.system.outbox.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderApprovalEventPayload(
    @JsonProperty String orderId,
    @JsonProperty String restaurantId,
    @JsonProperty BigDecimal price,
    @JsonProperty ZonedDateTime createdAt,
    @JsonProperty String restaurantOrderStatus,
    @JsonProperty List<OrderApprovalEventProduct> products) {}
