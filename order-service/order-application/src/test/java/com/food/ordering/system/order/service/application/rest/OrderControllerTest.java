package com.food.ordering.system.order.service.application.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@WebMvcTest(OrderController.class)
class OrderControllerTest extends BaseControllerTest {

  @Test
  void whenCreateOrderThrowBadRequestException_thenExpectedMessage() throws Exception {
    var expectedException =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Test Exception");
    expectedException.setInstance(new URI("/orders"));
    when(orderApplicationService.createOrder(any()))
        .thenThrow(new OrderDomainException("Test Exception"));
    var request =
        CreateOrderCommand.builder()
            .customerId(UUID.randomUUID())
            .restaurantId(UUID.randomUUID())
            .price(BigDecimal.ZERO)
            .address(new OrderAddress("", "", ""))
            .items(List.of())
            .build();
    restTestClient
        .post()
        .uri("/orders")
        .body(request)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(ProblemDetail.class)
        .isEqualTo(expectedException);
  }

  @Test
  void getOrderByTrackingID() throws Exception {
    var expectedException =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Test Exception");
    expectedException.setInstance(new URI("/orders"));
    when(orderApplicationService.createOrder(any()))
        .thenThrow(new OrderNotFoundException("Test Exception"));
    var request =
        CreateOrderCommand.builder()
            .customerId(UUID.randomUUID())
            .restaurantId(UUID.randomUUID())
            .price(BigDecimal.ZERO)
            .address(new OrderAddress("", "", ""))
            .items(List.of())
            .build();
    restTestClient
        .post()
        .uri("/orders")
        .body(request)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody(ProblemDetail.class)
        .isEqualTo(expectedException);
  }
}
