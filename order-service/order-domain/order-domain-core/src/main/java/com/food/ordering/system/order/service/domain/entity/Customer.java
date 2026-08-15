package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
  private String username;
  private String firstName;
  private String lastName;

  public Customer(CustomerId customerId, String username, String firstName, String lastName) {
    super.setId(customerId);
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Customer(CustomerId customerId) {
    super.setId(customerId);
  }

  private Customer(Builder builder) {
    setId(builder.id);
    username = builder.username;
    firstName = builder.firstName;
    lastName = builder.lastName;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getUsername() {
    return username;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public static final class Builder {
    private CustomerId id;
    private String username;
    private String firstName;
    private String lastName;

    private Builder() {}

    public Builder id(CustomerId val) {
      id = val;
      return this;
    }

    public Builder username(String val) {
      username = val;
      return this;
    }

    public Builder firstName(String val) {
      firstName = val;
      return this;
    }

    public Builder lastName(String val) {
      lastName = val;
      return this;
    }

    public Customer build() {
      return new Customer(this);
    }
  }
}
