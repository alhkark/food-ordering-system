package com.food.ordering.system.restaurant.service.domain.outbox.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderEventPayload(
    @JsonProperty String orderId,
    @JsonProperty String restaurantId,
    @JsonProperty ZonedDateTime createdAt,
    @JsonProperty String orderApprovalStatus,
    @JsonProperty List<String> failureMessages) {}
