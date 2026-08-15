package com.food.ordering.system.customer.service.dataaccess.customer.mapper;

import com.food.ordering.system.customer.service.dataaccess.customer.entity.CustomerEntity;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UtilsMapperCommon.class)
public interface CustomerDataAccessMapper {

  @Mapping(source = "id", target = "customerId")
  Customer toCustomer(CustomerEntity customerEntity);

  @Mapping(source = "id.value", target = "id")
  CustomerEntity toCustomerEntity(Customer customer);
}
