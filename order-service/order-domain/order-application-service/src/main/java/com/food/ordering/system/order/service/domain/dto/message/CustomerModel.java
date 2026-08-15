package com.food.ordering.system.order.service.domain.dto.message;

import lombok.Builder;

@Builder
public record CustomerModel(String id, String username, String firstName, String lastName) {}
