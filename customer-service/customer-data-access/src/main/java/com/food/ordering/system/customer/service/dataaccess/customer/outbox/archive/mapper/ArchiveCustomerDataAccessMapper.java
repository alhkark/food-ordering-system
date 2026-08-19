package com.food.ordering.system.customer.service.dataaccess.customer.outbox.archive.mapper;

import com.food.ordering.system.customer.service.dataaccess.customer.outbox.archive.entity.ArchiveCustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.mapper.UtilsMapperCommon;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface ArchiveCustomerDataAccessMapper {

  ArchiveCustomerOutboxMessage toArchiveEntity(CustomerOutboxMessage customerOutboxMessage);
}
