package com.food.ordering.system.customer.service.dataaccess.customer.adapter;

import com.food.ordering.system.customer.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.food.ordering.system.customer.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.customer.service.domain.exception.CustomerExistException;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

  private final CustomerJpaRepository customerJpaRepository;

  private final CustomerDataAccessMapper customerDataAccessMapper;

  @Override
  public Customer createCustomer(Customer customer) {
    if (customerJpaRepository.existsById(customer.getId().value())) {
      throw new CustomerExistException("Customer with id " + customer.getId() + " already exists");
    }
    return customerDataAccessMapper.toCustomer(
        customerJpaRepository.save(customerDataAccessMapper.toCustomerEntity(customer)));
  }
}
