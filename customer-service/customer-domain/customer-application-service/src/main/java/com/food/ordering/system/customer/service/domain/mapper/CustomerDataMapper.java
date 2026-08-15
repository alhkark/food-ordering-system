package com.food.ordering.system.customer.service.domain.mapper;

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UtilsMapperCommon.class)
public interface CustomerDataMapper {

  @Mapping(source = "customerId", target = "id")
  Customer toCustomer(CreateCustomerCommand createCustomerCommand);

  @Mapping(source = "customer.id.value", target = "customerId")
  CreateCustomerResponse toCreateCustomerResponse(Customer customer, String message);
}
