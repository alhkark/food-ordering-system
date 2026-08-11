package com.food.ordering.system.order.service.domain;

import static com.food.ordering.system.domain.DomainConstants.FAILURE_MESSAGE_DELIMITER;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@RequiredArgsConstructor
@Service
public class RestaurantApprovalResponseMessageListenerImpl
    implements RestaurantApprovalResponseMessageListener {

  private final OrderApprovalSaga orderApprovalSaga;

  @Override
  public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
    orderApprovalSaga.process(restaurantApprovalResponse);
    log.info("Order is approved for order id: {}", restaurantApprovalResponse.orderId());
  }

  @Override
  public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
    orderApprovalSaga.rollback(restaurantApprovalResponse);
    log.info(
        "Order Approval Saga rollback operation is completed for order id: {} with failure messages: {}",
        restaurantApprovalResponse.orderId(),
        String.join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponse.failureMessage()));
  }
}
