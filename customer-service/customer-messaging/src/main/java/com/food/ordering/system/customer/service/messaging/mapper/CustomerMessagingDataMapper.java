package com.food.ordering.system.customer.service.messaging.mapper;

import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMessagingDataMapper {

  @Mapping(target = "id", source = "customer.id.value")
  @Mapping(target = "username", source = "customer.username")
  @Mapping(target = "firstName", source = "customer.firstName")
  @Mapping(target = "lastName", source = "customer.lastName")
  CustomerAvroModel toCustomerAvroModel(CustomerCreatedEvent customerCreatedEvent);
}
