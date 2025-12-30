package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

  private static final String UTC = "UTC";

  @Override
  public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
    validateRestaurant(restaurant);
    setOrderProductInformation(order, restaurant);
    order.validateOrder();
    order.initializeOrder();
    log.info("Order with id {} is initiated", order.getId().value());
    return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
  }

  @Override
  public OrderPaidEvent payOrder(Order order) {
    order.pay();
    log.info("Order with id: {} is paid", order.getId().value());
    return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
  }

  @Override
  public void approveOrder(Order order) {
    order.approve();
    log.info("Order with id: {} is approved", order.getId().value());
  }

  @Override
  public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
    order.initCancel(failureMessages);
    log.info("Order with id: {} is cancelling", order.getId().value());
    return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
  }

  @Override
  public void cancelOrder(Order order, List<String> failureMessages) {
    order.cancel(failureMessages);
    log.info("Order with id: {} is cancelled", order.getId().value());
  }

  private void validateRestaurant(Restaurant restaurant) {
    if (!restaurant.isActive()) {
      throw new OrderDomainException(
          "Restaurant with id %s is currently not active!".formatted(restaurant.getId().value()));
    }
  }

  private void setOrderProductInformation(Order order, Restaurant restaurant) {
    var restaurantProducts = new HashMap<ProductId, Product>();
    restaurant
        .getProducts()
        .forEach(
            restaurantProduct -> {
              restaurantProducts.put(restaurantProduct.getId(), restaurantProduct);
            });
    order
        .getItems()
        .forEach(
            orderItem -> {
              var restaurantProduct = restaurantProducts.get(orderItem.getProduct().getId());
              if (restaurantProduct == null) {
                throw new OrderDomainException(
                    "Product with id %s is not found in restaurant with id %s"
                        .formatted(
                            orderItem.getProduct().getId().value(), restaurant.getId().value()));
              }
              orderItem
                  .getProduct()
                  .updateWithConfirmedNameAndPrice(
                      restaurantProduct.getName(), restaurantProduct.getPrice());
            });
  }
}
