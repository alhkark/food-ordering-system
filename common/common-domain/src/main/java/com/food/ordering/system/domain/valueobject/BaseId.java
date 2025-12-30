package com.food.ordering.system.domain.valueobject;

public abstract class BaseId<T> {
  private final T value;

  protected BaseId(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof BaseId<?> that)) return false;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
