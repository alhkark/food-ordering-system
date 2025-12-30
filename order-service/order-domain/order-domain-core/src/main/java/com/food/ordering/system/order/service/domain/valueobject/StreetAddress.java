package com.food.ordering.system.order.service.domain.valueobject;

import java.util.UUID;

public record StreetAddress(UUID id, String street, String postalCode, String city) {

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StreetAddress address)) return false;
    return street.equals(address.street())
        && street.equals(address.postalCode)
        && city.equals(address.city());
  }

  @Override
  public int hashCode() {
    return (street + postalCode + city).hashCode();
  }
}
