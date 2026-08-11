package com.food.ordering.system.domain.event.publisher;

import com.food.ordering.system.domain.event.DomainEvent;

public final class EmptyEvent implements DomainEvent<Void> {

  public static final EmptyEvent INSTANCE = new EmptyEvent();

  private EmptyEvent() {}
}
