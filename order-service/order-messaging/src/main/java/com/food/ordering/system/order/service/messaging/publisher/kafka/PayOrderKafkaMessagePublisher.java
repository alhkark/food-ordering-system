package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  public void publish(OrderPaidEvent domainEvent) {
    var orderId = domainEvent.order().getId().value().toString();
    log.info("Received OrderPaidEvent for order id: {}", orderId);
    try {
      var restaurantApprovalRequestAvroModel =
          orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

      kafkaProducer.send(
          orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
          orderId,
          restaurantApprovalRequestAvroModel,
          kafkaMessageHelper.getCallbackHandler(restaurantApprovalRequestAvroModel, orderId));
      log.info(
          "RestaurantApprovalRequestAvroModel sent to Kafka for orderId: {}",
          restaurantApprovalRequestAvroModel.getOrderId());
    } catch (Exception e) {
      log.error(
          "Error on publishing payment request to Kafka for orderId: {}, error: {}",
          orderId,
          e.getMessage());
    }
  }
}
