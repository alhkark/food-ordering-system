package com.food.ordering.system.customer.service.dataaccess.customer.outbox.mapper;

import com.food.ordering.system.customer.service.dataaccess.customer.outbox.entity.CustomerOutboxEntity;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface CustomerOutboxDataAccessMapper {

  CustomerOutboxEntity toCustomerOutboxEntity(CustomerOutboxMessage customerOutboxMessage);

  CustomerOutboxMessage toCustomerOutboxMessage(CustomerOutboxEntity customerOutboxEntity);
}
