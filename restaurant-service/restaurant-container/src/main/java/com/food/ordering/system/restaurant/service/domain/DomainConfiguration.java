package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.restaurant.service.domain.annotation.DomainService;
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
