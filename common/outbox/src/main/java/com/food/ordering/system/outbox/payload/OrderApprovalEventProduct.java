package com.food.ordering.system.outbox.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OrderApprovalEventProduct(@JsonProperty String id, @JsonProperty Integer quantity) {}
