package com.food.ordering.system.order.service.dataaccess.customer.mapper;

import com.food.ordering.system.order.service.dataaccess.customer.entity.CustomerEntity;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.mapper.UtilsMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapper.class})
public interface CustomerDataAccessMapper {

  Customer toCustomer(CustomerEntity customerEntity);
}
