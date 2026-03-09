package com.food.ordering.system.order.service.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.CreateOrderCommandMapper;
import com.food.ordering.system.order.service.domain.mapper.OrderMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderApplicationServiceTest extends BaseTest {

  @Autowired OrderApplicationService orderApplicationService;

  @Autowired OrderMapper orderMapper;

  @Autowired CreateOrderCommandMapper createOrderCommandMapper;

  CreateOrderCommand createOrderCommand;
  CreateOrderCommand createOrderCommandWrongPrice;
  CreateOrderCommand createOrderCommandWrongProductPrice;
  Customer customer;
  Restaurant restaurantResponse;
  final UUID CUSTOMER_ID = UUID.fromString("b134c0f3-2c55-4e21-9834-af684f87d4de");
  final UUID RESTAURANT_ID = UUID.fromString("6e446872-bbeb-43f4-8915-6f5ac60f80c0");
  final UUID PRODUCT_ID = UUID.fromString("c3304d12-2f65-4948-baed-1c871bb7025e");
  final UUID ORDER_ID = UUID.fromString("4ac18f1a-f6cd-4eda-b73f-1536eed16648");
  final BigDecimal PRICE = new BigDecimal("200.00");

  @BeforeAll
  public void init() {
    var orderAddress = new OrderAddress("street 1", "12345", "London");
    var orderItem_1 =
        new OrderItem(PRODUCT_ID, 1, new BigDecimal("50.00"), new BigDecimal("50.00"));
    var orderItem_2 =
        new OrderItem(PRODUCT_ID, 3, new BigDecimal("50.00"), new BigDecimal("150.00"));
    createOrderCommand =
        new CreateOrderCommand(
            CUSTOMER_ID, RESTAURANT_ID, PRICE, List.of(orderItem_1, orderItem_2), orderAddress);

    createOrderCommandWrongPrice =
        CreateOrderCommand.builder()
            .customerId(CUSTOMER_ID)
            .restaurantId(RESTAURANT_ID)
            .price(new BigDecimal("250.00"))
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("50.00"))
                        .build(),
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(3)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("150.00"))
                        .build()))
            .address(orderAddress)
            .build();

    createOrderCommandWrongProductPrice =
        CreateOrderCommand.builder()
            .customerId(CUSTOMER_ID)
            .restaurantId(RESTAURANT_ID)
            .price(new BigDecimal("210.00"))
            .items(
                List.of(
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(1)
                        .price(new BigDecimal("60.00"))
                        .subTotal(new BigDecimal("60.00"))
                        .build(),
                    OrderItem.builder()
                        .productId(PRODUCT_ID)
                        .quantity(3)
                        .price(new BigDecimal("50.00"))
                        .subTotal(new BigDecimal("150.00"))
                        .build()))
            .address(orderAddress)
            .build();

    customer = new Customer();
    customer.setId(new CustomerId(CUSTOMER_ID));

    restaurantResponse =
        Restaurant.builder()
            .id(new RestaurantId(createOrderCommand.restaurantId()))
            .products(
                List.of(
                    new Product(
                        new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                    new Product(
                        new ProductId(PRODUCT_ID),
                        "product-2",
                        new Money(new BigDecimal("50.00")))))
            .active(true)
            .build();

    var order = createOrderCommandMapper.toOrder(createOrderCommand);
    order.setId(new OrderId(ORDER_ID));
  }

  @BeforeEach
  public void setup() {
    when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    when(restaurantRepository.findRestaurantInformation(
            createOrderCommandMapper.toRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(
            i -> {
              var orderSaved = (Order) i.getArguments()[0];
              orderSaved.setId(new OrderId(ORDER_ID));
              return orderSaved;
            });
  }

  @Test
  void createOrder_whenValidData_thenCreateOrder() {
    var createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
    assertThat(createOrderResponse.orderStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(createOrderResponse.message()).isEqualTo("Order created successfully");
    assertThat(createOrderResponse.orderTrackingId()).isNotNull();
  }

  @Test
  void createOrder_whenWrongPrice_thenException() {
    assertThatThrownBy(() -> orderApplicationService.createOrder(createOrderCommandWrongPrice))
        .isInstanceOf(OrderDomainException.class)
        .hasMessage(
            "Total price: 250.00 is not equal to order items total: %s",
            createOrderCommandWrongPrice.items().stream()
                .map(OrderItem::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
  }

  @Test
  void createOrder_whenWrongProductPrice_thenException() {
    assertThatThrownBy(
            () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice))
        .isInstanceOf(OrderDomainException.class)
        .hasMessage("Order item price: 60.00 is not valid for product %s", PRODUCT_ID);
  }

  @Test
  void createOrder_whenPassiveRestaurant_thenException() {
    var restaurantResponse =
        Restaurant.builder()
            .id(new RestaurantId(createOrderCommand.restaurantId()))
            .products(
                List.of(
                    new Product(
                        new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                    new Product(
                        new ProductId(PRODUCT_ID),
                        "product-2",
                        new Money(new BigDecimal("50.00")))))
            .active(false)
            .build();
    when(restaurantRepository.findRestaurantInformation(
            createOrderCommandMapper.toRestaurant(createOrderCommand)))
        .thenReturn(Optional.of(restaurantResponse));
    assertThatThrownBy(() -> orderApplicationService.createOrder(createOrderCommand))
        .isInstanceOf(OrderDomainException.class)
        .hasMessage("Restaurant with id %s is currently not active!", RESTAURANT_ID);
  }
}
