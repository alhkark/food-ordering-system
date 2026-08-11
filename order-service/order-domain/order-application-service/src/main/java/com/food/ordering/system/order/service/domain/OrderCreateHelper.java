package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.CreateOrderCommandMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateHelper {

  private final OrderDomainService orderDomainService;

  private final OrderRepository orderRepository;

  private final CustomerRepository customerRepository;

  private final RestaurantRepository restaurantRepository;

  private final CreateOrderCommandMapper createOrderCommandMapper;

  @Transactional
  public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
    checkCustomer(createOrderCommand.customerId());
    Restaurant restaurant = checkRestaurant(createOrderCommand);
    Order order = createOrderCommandMapper.toOrder(createOrderCommand);
    OrderCreatedEvent orderCreatedEvent =
        orderDomainService.validateAndInitiateOrder(order, restaurant);
    saveOrder(order);
    log.info("Order is created with id: {}", orderCreatedEvent.order().getId().value());
    return orderCreatedEvent;
  }

  private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
    Restaurant restaurant = createOrderCommandMapper.toRestaurant(createOrderCommand);
    Optional<Restaurant> optionalRestaurant =
        restaurantRepository.findRestaurantInformation(restaurant);
    if (optionalRestaurant.isEmpty()) {
      log.warn(
          "Could not find restaurant with restaurant id: {}", createOrderCommand.restaurantId());
      throw new OrderDomainException(
          "Could not find restaurant with restaurant id: " + createOrderCommand.restaurantId());
    }
    return optionalRestaurant.get();
  }

  private void checkCustomer(UUID customerId) {
    Optional<Customer> customer = customerRepository.findCustomer(customerId);
    if (customer.isEmpty()) {
      log.warn("Could not find customer with customer id: {}", customerId);
      throw new OrderDomainException("Could not find customer with customer id: " + customer);
    }
  }

  private Order saveOrder(Order order) {
    Order orderResult = orderRepository.save(order);
    if (orderResult == null) {
      log.error("Could not save order!");
      throw new OrderDomainException("Could not save order!");
    }
    log.info("Order is saved with id: {}", orderResult.getId().value());
    return orderResult;
  }
}
