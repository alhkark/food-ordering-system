package com.food.ordering.system.order.service.domain.ports;

import com.food.ordering.system.order.service.domain.OrderDomainService;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.CreateOrderCommandMapper;
import com.food.ordering.system.order.service.domain.mapper.OrderMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderCreateCommandHandler {

  private final OrderDomainService orderDomainService;

  private final OrderRepository orderRepository;

  private final CustomerRepository customerRepository;

  private final RestaurantRepository restaurantRepository;

  private final CreateOrderCommandMapper createOrderCommandMapper;

  private final OrderMapper orderMapper;

  public OrderCreateCommandHandler(
      OrderDomainService orderDomainService,
      OrderRepository orderRepository,
      CustomerRepository customerRepository,
      RestaurantRepository restaurantRepository,
      CreateOrderCommandMapper createOrderCommandMapper,
      OrderMapper orderMapper) {
    this.orderDomainService = orderDomainService;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.restaurantRepository = restaurantRepository;
    this.createOrderCommandMapper = createOrderCommandMapper;
    this.orderMapper = orderMapper;
  }

  @Transactional
  public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
    checkCustomer(createOrderCommand.customerId());
    var restaurant = checkRestaurant(createOrderCommand);
    var order = createOrderCommandMapper.toOrder(createOrderCommand);
    var orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
    var orderResult = saveOrder(order);
    log.info("Order is created with id: {}", orderResult.getId().value());
    return orderMapper.toCreateOrderResponse(orderResult);
  }

  private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
    var restaurant = createOrderCommandMapper.toRestaurant(createOrderCommand);
    var optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
    return optionalRestaurant.orElseThrow(
        () -> {
          log.warn("Could not find restaurant with id: {}", createOrderCommand.restaurantId());
          return new OrderDomainException("Restaurant not found");
        });
  }

  private void checkCustomer(UUID customerId) {
    var optionalCustomer = customerRepository.findCustomer(customerId);
    if (optionalCustomer.isEmpty()) {
      log.warn("Could not find customer with id: {}", customerId);
      throw new OrderDomainException("Could not find customer with id: " + customerId);
    }
  }

  private Order saveOrder(Order order) {
    var savedOrder = orderRepository.save(order);
    if (savedOrder == null) {
      log.error("Could not save order!");
      throw new OrderDomainException("Could not save order!");
    }
    log.info("Order with id: {} is saved", savedOrder.getId().value());
    return savedOrder;
  }
}
