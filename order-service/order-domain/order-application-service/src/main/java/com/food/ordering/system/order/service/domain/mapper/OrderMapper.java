package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.mapper.UtilsMapperCommon;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.message.CustomerModel;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
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

  @Mapping(source = "order.customerId.value", target = "customerId")
  @Mapping(source = "order.id.value", target = "orderId")
  @Mapping(source = "order.price.amount", target = "price")
  @Mapping(
      target = "paymentOrderStatus",
      expression =
          "java(com.food.ordering.system.domain.valueobject.PaymentOrderStatus.PENDING.name())")
  OrderPaymentEventPayload toOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent);

  @Mapping(source = "order.id.value", target = "orderId")
  @Mapping(source = "order.restaurantId.value", target = "restaurantId")
  @Mapping(source = "order.price.amount", target = "price")
  @Mapping(
      target = "restaurantOrderStatus",
      expression =
          "java(com.food.ordering.system.domain.valueobject.RestaurantOrderStatus.PAID.name())")
  @Mapping(source = "order.items", target = "products")
  OrderApprovalEventPayload toOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent);

  @Mapping(source = "order.customerId.value", target = "customerId")
  @Mapping(source = "order.id.value", target = "orderId")
  @Mapping(source = "order.price.amount", target = "price")
  @Mapping(
      target = "paymentOrderStatus",
      expression =
          "java(com.food.ordering.system.domain.valueobject.PaymentOrderStatus.CANCELLED.name())")
  OrderPaymentEventPayload toOrderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent);

  @Mapping(source = "product.id.value", target = "id")
  OrderApprovalEventProduct toOrderApprovalEventProduct(OrderItem orderItem);

  Customer toCustomer(CustomerModel customerModel);
}
