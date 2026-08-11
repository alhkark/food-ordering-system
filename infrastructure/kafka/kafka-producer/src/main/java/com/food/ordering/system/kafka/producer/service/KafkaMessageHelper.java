package com.food.ordering.system.kafka.producer.service;

import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.outbox.OutboxStatus;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageHelper {

  private final JsonMapper jsonMapper;

  public <T> T getOrderEventPayload(String payload, Class<T> outputType) {
    try {
      return jsonMapper.readValue(payload, outputType);
    } catch (JacksonException e) {
      log.error("Could not read {} object!", outputType.getName(), e);
      throw new OrderDomainException("Could not read " + outputType.getName() + " object!", e);
    }
  }

  public <T, U> BiConsumer<SendResult<String, T>, Throwable> getCallbackHandler(
      T avroModel, U outboxMessage, BiConsumer<U, OutboxStatus> outboxCallback, String orderId) {
    return (SendResult<String, T> result, Throwable e) -> {
      if (e != null) {
        log.error(
            "Error while sending message: {} and outbox type: {}",
            avroModel.toString(),
            outboxMessage.getClass().getName(),
            e);
        outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
      } else {
        RecordMetadata metadata = result.getRecordMetadata();
        log.info(
            "Received successful response from Kafka for order id: {}"
                + " Topic: {} Partition: {} Offset: {} Timestamp: {}",
            orderId,
            metadata.topic(),
            metadata.partition(),
            metadata.offset(),
            metadata.timestamp());
        outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
      }
    };
  }
}
