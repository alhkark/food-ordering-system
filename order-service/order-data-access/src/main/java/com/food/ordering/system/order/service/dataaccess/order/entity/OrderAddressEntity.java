package com.food.ordering.system.order.service.dataaccess.order.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_address")
@Entity
public class OrderAddressEntity {

  @Id private UUID id;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "ORDER_ID")
  private OrderEntity order;

  private String street;
  private String postalCode;
  private String city;

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof OrderAddressEntity that)) return false;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
