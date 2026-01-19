package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(source = "trackingId", target = "orderTrackingId")
  @Mapping(target = "message", expression = "java(\"Order created successfully.\")")
  CreateOrderResponse toCreateOrderResponse(Order order);

  TrackOrderResponse toTrackOrderResponse(Order order);

  default UUID toOrderTrackingId(TrackingId trackingId) {
    return trackingId.trackingId();
  }
}
