package com.food.ordering.system.order.service.dataaccess.order.mapper;

import static com.food.ordering.system.domain.DomainConstants.FAILURE_MESSAGE_DELIMITER;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.mapper.UtilsMapperCommon;
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
    uses = {UtilsMapper.class, UtilsMapperCommon.class})
public interface OrderDataAccessMapper {

  @Mapping(target = "id", expression = "java(order.getId().value())")
  @Mapping(target = "customerId", source = "customerId.value")
  @Mapping(target = "restaurantId", source = "restaurantId.value")
  @Mapping(target = "trackingId", source = "trackingId.trackingId")
  @Mapping(source = "deliveryAddress", target = "address")
  OrderEntity toOrderEntity(Order order);

  @InheritInverseConfiguration
  @Mapping(source = "id", target = "orderId")
  Order toOrder(OrderEntity orderEntity);

  @Mapping(target = "order", ignore = true)
  OrderAddressEntity toOrderAddressEntity(StreetAddress streetAddress);

  @Mapping(target = "id", expression = "java(orderItem.getId().orderId())")
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "productId", source = "product.id.value")
  OrderItemEntity toOrderItemEntity(OrderItem orderItem);

  @Mapping(source = "id", target = "orderItemId")
  @Mapping(source = "order", target = "orderId")
  @Mapping(source = "productId", target = "product")
  OrderItem toOrderItem(OrderItemEntity orderItemEntity);

  default String toFailureMessagesDao(List<String> failureMessages) {
    return failureMessages == null ? "" : String.join(FAILURE_MESSAGE_DELIMITER, failureMessages);
  }

  default List<String> toFailureMessages(String failureMessages) {
    return failureMessages == null || failureMessages.isBlank()
        ? new ArrayList<>()
        : Arrays.stream(failureMessages.split(FAILURE_MESSAGE_DELIMITER))
            .map(String::trim)
            .collect(Collectors.toCollection(ArrayList::new));
  }

  default OrderId toOrderId(OrderEntity orderEntity) {
    return new OrderId(orderEntity.getId());
  }

  @AfterMapping
  default void setOrderEntity(@MappingTarget OrderEntity orderEntity) {
    orderEntity.getAddress().setOrder(orderEntity);
    orderEntity
        .getItems()
        .forEach(
            orderItem -> {
              orderItem.setOrder(orderEntity);
            });
  }
}
