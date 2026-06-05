package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapper.class, UtilsMapperCommon.class})
public interface OrderMapper {

  @Mapping(source = "order.trackingId", target = "orderTrackingId")
  CreateOrderResponse toCreateOrderResponse(Order order, String message);

  @Mapping(source = "trackingId", target = "orderTrackingId")
  TrackOrderResponse toTrackOrderResponse(Order order);
}
