package com.food.ordering.system.domain.valueobject;

import java.util.List;

public record OrderPreferences(
    List<String> removeIngredients,
    List<String> addIngredients,
    SpiceLevel spiceLevel,
    String specialInstructions,
    String deliveryInstructions) {
  private OrderPreferences(Builder builder) {
    this(
        builder.removeIngredients,
        builder.addIngredients,
        builder.spiceLevel,
        builder.specialInstructions,
        builder.deliveryInstructions);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private List<String> removeIngredients;
    private List<String> addIngredients;
    private SpiceLevel spiceLevel;
    private String specialInstructions;
    private String deliveryInstructions;

    private Builder() {}

    public Builder removeIngredients(List<String> val) {
      removeIngredients = val;
      return this;
    }

    public Builder addIngredients(List<String> val) {
      addIngredients = val;
      return this;
    }

    public Builder spiceLevel(SpiceLevel val) {
      spiceLevel = val;
      return this;
    }

    public Builder specialInstructions(String val) {
      specialInstructions = val;
      return this;
    }

    public Builder deliveryInstructions(String val) {
      deliveryInstructions = val;
      return this;
    }

    public OrderPreferences build() {
      return new OrderPreferences(this);
    }
  }
}
