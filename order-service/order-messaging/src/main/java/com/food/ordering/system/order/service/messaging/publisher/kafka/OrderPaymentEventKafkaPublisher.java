package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentEventKafkaPublisher implements PaymentRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  public void publish(
      OrderPaymentOutboxMessage orderPaymentOutboxMessage,
      BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback) {
    OrderPaymentEventPayload orderPaymentEventPayload =
        kafkaMessageHelper.getOrderEventPayload(
            orderPaymentOutboxMessage.getPayload(), OrderPaymentEventPayload.class);

    String sagaId = orderPaymentOutboxMessage.getSagaId().toString();

    log.info(
        "Received OrderPaymentOutboxMessage for order id: {} and saga id: {}",
        orderPaymentEventPayload.orderId(),
        sagaId);

    try {
      PaymentRequestAvroModel paymentRequestAvroModel =
          orderMessagingDataMapper.toPaymentRequestAvroModel(orderPaymentEventPayload, sagaId);

      kafkaProducer.send(
          orderServiceConfigData.getPaymentRequestTopicName(),
          sagaId,
          paymentRequestAvroModel,
          kafkaMessageHelper.getCallbackHandler(
              paymentRequestAvroModel,
              orderPaymentOutboxMessage,
              outboxCallback,
              orderPaymentEventPayload.orderId()));

      log.info(
          "OrderPaymentEventPayload sent to Kafka for order id: {} and saga id: {}",
          orderPaymentEventPayload.orderId(),
          sagaId);
    } catch (Exception e) {
      log.error(
          "Error while sending OrderPaymentEventPayload"
              + " to kafka with order id: {} and saga id: {}, error: {}",
          orderPaymentEventPayload.orderId(),
          sagaId,
          e.getMessage());
    }
  }
}
