package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.mapper.UtilsMapper;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {UtilsMapper.class})
public abstract class OrderDataAccessMapper {

  private static final String FAILURE_MESSAGE_DELIMiTER = ",";

  @Mapping(target = "id", expression = "java(order.getId().value())")
  @Mapping(target = "customerId", expression = "java(order.getCustomerId().value())")
  @Mapping(target = "restaurantId", expression = "java(order.getRestaurantId().value())")
  @Mapping(target = "trackingId", expression = "java(order.getTrackingId().trackingId())")
  @Mapping(source = "deliveryAddress", target = "address")
  public abstract OrderEntity toOrderEntity(Order order);

  @InheritInverseConfiguration
  @Mapping(source = "id", target = "orderId")
  public abstract Order toOrder(OrderEntity orderEntity);

  @Mapping(target = "order", ignore = true)
  public abstract OrderAddressEntity toOrderAddressEntity(StreetAddress streetAddress);

  @Mapping(target = "id", expression = "java(orderItem.getId().orderId())")
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "productId", expression = "java(orderItem.getProduct().getId().value())")
  public abstract OrderItemEntity toOrderItemEntity(OrderItem orderItem);

  @Mapping(source = "id", target = "orderItemId")
  @Mapping(source = "order", target = "orderId")
  @Mapping(source = "productId", target = "product")
  public abstract OrderItem toOrderItem(OrderItemEntity orderItemEntity);

  protected String toFailureMessagesDao(List<String> failureMessages) {
    return failureMessages == null ? "" : String.join(FAILURE_MESSAGE_DELIMiTER, failureMessages);
  }

  protected List<String> toFailureMessages(String failureMessages) {
    return failureMessages == null
        ? new ArrayList<>()
        : Arrays.stream(failureMessages.split(FAILURE_MESSAGE_DELIMiTER))
            .map(String::trim)
            .collect(Collectors.toCollection(ArrayList::new));
  }

  protected OrderId toOrderId(OrderEntity orderEntity) {
    return new OrderId(orderEntity.getId());
  }

  @AfterMapping
  protected void setOrderEntity(@MappingTarget OrderEntity orderEntity) {
    orderEntity.getAddress().setOrder(orderEntity);
    orderEntity
        .getItems()
        .forEach(
            orderItem -> {
              orderItem.setOrder(orderEntity);
            });
  }
}
