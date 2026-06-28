package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderSagaHelper {

  private final OrderRepository orderRepository;

  public Order findOrder(String orderId) {
    return orderRepository
        .findById(new OrderId(UUID.fromString(orderId)))
        .orElseThrow(
            () -> {
              log.error("Order with id: {} could not be found!", orderId);
              return new OrderNotFoundException(
                  "Order with id " + orderId + " could not be found!");
            });
  }

  public void saveOrder(Order order) {
    orderRepository.save(order);
  }
}
