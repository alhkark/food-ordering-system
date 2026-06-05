package com.food.ordering.system.payment.service.dataaccess.creditentry.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.valueobject.CreditEntryId;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface CreditEntryDataAccessMapper {

  @Mapping(source = "creditEntryEntity.id", target = "creditEntryId")
  CreditEntry toCreditEntry(CreditEntryEntity creditEntryEntity);

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "customerId", source = "customerId.value")
  CreditEntryEntity toCreditEntryEntity(CreditEntry creditEntry);

  default CreditEntryId toCreditEntryId(UUID id) {
    return new CreditEntryId(id);
  }
}
