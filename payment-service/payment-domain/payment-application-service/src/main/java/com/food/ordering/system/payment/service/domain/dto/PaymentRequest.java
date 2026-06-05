package com.food.ordering.system.payment.service.domain.dto;

import com.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentRequest {
  private String id;
  private String sagaId;
  private String orderId;
  private String customerId;
  private BigDecimal price;
  private Instant createdAt;
  @Setter private PaymentOrderStatus paymentOrderStatus;
}
