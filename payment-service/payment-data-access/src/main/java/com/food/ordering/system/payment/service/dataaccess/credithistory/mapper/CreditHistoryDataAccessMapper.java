package com.food.ordering.system.payment.service.dataaccess.credithistory.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapperCommon.class})
public interface CreditHistoryDataAccessMapper {

  @Mapping(source = "creditHistoryEntity.id", target = "creditHistoryId")
  @Mapping(source = "creditHistoryEntity.type", target = "transactionType")
  CreditHistory toCreditHistory(CreditHistoryEntity creditHistoryEntity);

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "customerId", source = "customerId.value")
  @Mapping(source = "creditHistory.transactionType", target = "type")
  CreditHistoryEntity toCreditHistoryEntity(CreditHistory creditHistory);

  default CreditHistoryId toCreditHistoryId(UUID id) {
    return new CreditHistoryId(id);
  }
}
