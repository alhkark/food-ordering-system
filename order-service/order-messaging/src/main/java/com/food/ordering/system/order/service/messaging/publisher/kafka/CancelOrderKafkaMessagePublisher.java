package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CancelOrderKafkaMessagePublisher
    implements OrderCancelledPaymentRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  public void publish(OrderCancelledEvent domainEvent) {
    var orderId = domainEvent.order().getId().value().toString();
    log.info("Received OrderCancelledEvent for order id: {}", orderId);
    try {
      var paymentRequestAvroModel =
          orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(domainEvent);

      kafkaProducer.send(
          orderServiceConfigData.getPaymentRequestTopicName(),
          orderId,
          paymentRequestAvroModel,
          kafkaMessageHelper.getCallbackHandler(paymentRequestAvroModel, orderId));
      log.info(
          "PaymentRequestAvroModel sent to Kafka for orderId: {}",
          paymentRequestAvroModel.getOrderId());
    } catch (Exception e) {
      log.error(
          "Error on publishing payment request to Kafka for orderId: {}, error: {}",
          orderId,
          e.getMessage());
    }
  }
}
