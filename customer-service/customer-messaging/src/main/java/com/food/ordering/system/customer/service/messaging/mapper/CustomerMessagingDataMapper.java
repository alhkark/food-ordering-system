package com.food.ordering.system.customer.service.messaging.mapper;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerEventPayload;
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMessagingDataMapper {

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  CustomerAvroModel toCustomerAvroModel(CustomerEventPayload customerEventPayload);
}
