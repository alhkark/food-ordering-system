package com.food.ordering.system.customer.service.domain.outbox.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CustomerEventPayload(
    @JsonProperty UUID customerId,
    @JsonProperty String username,
    @JsonProperty String firstName,
    @JsonProperty String lastName) {}
