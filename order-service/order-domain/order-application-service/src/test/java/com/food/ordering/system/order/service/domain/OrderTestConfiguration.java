package com.food.ordering.system.order.service.domain;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.food.ordering.system")
public class OrderTestConfiguration {

  @Bean
  OrderDomainService orderDomainService() {
    return new OrderDomainServiceImpl();
  }
}
