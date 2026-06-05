package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.payment.service.domain.annotation.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
    basePackages = {"com.food.ordering.system"},
    includeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ANNOTATION,
          classes = {DomainService.class})
    })
public class DomainConfiguration {}
