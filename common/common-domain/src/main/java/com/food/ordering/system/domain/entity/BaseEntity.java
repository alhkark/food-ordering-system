package com.food.ordering.system.domain.entity;

public abstract class BaseEntity<ID> {
  private ID id;

  public ID getId() {
    return id;
  }

  public void setId(ID id) {
    this.id = id;
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof BaseEntity<?> that)) return false;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
