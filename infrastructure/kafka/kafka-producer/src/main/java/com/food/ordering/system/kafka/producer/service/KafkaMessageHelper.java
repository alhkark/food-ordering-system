package com.food.ordering.system.kafka.producer.service;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessageHelper {

  public <T> Consumer<SendResult<String, T>> getCallbackHandler(T avroModel, String orderId) {
    return (result) -> {
      var recordMetadata = result.getRecordMetadata();
      log.info(
          "Received successful response from kafka for order id: {} topic: {} partition: {} offset: {} timestamp: {}",
          orderId,
          recordMetadata.topic(),
          recordMetadata.partition(),
          recordMetadata.offset(),
          recordMetadata.timestamp());
    };
  }
}
