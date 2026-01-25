package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = OrderTestConfiguration.class)
public class BaseTest {

  @MockitoBean
  OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

  @MockitoBean
  OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

  @MockitoBean
  OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

  @MockitoBean OrderRepository orderRepository;

  @MockitoBean CustomerRepository customerRepository;

  @MockitoBean RestaurantRepository restaurantRepository;
}
