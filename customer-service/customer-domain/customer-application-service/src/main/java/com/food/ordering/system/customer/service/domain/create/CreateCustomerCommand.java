package com.food.ordering.system.customer.service.domain.create;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateCustomerCommand(
    @NotNull UUID customerId,
    @NotNull String username,
    @NotNull String firstName,
    @NotNull String lastName) {}
