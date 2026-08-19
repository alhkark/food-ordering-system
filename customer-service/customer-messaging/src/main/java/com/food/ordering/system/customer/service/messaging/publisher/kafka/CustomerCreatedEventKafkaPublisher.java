package com.food.ordering.system.customer.service.messaging.publisher.kafka;

import com.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerEventPayload;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import com.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCreatedEventKafkaPublisher implements CustomerMessagePublisher {

  private final CustomerMessagingDataMapper customerMessagingDataMapper;

  private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;

  private final CustomerServiceConfigData customerServiceConfigData;

  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  public void publish(
      CustomerOutboxMessage customerOutboxMessage,
      BiConsumer<CustomerOutboxMessage, OutboxStatus> outboxCallback) {
    CustomerEventPayload customerEventPayload =
        kafkaMessageHelper.getOrderEventPayload(
            customerOutboxMessage.getPayload(), CustomerEventPayload.class);

    log.info(
        "Received CustomerOutboxMessage for customer id: {}", customerEventPayload.customerId());

    try {
      CustomerAvroModel customerAvroModel =
          customerMessagingDataMapper.toCustomerAvroModel(customerEventPayload);

      kafkaProducer.send(
          customerServiceConfigData.getCustomerTopicName(),
          customerAvroModel.getCustomerId().toString(),
          customerAvroModel,
          kafkaMessageHelper.getCallbackHandler(
              customerAvroModel,
              customerOutboxMessage,
              outboxCallback,
              customerEventPayload.customerId().toString()));

      log.info("CustomerAvroModel sent to kafka for customer id: {}", customerAvroModel.getId());
    } catch (Exception e) {
      log.error(
          "Error while sending CustomerAvroModel message"
              + " to kafka with customer id: {}, error: {}",
          customerEventPayload.customerId(),
          e.getMessage());
    }
  }
}
