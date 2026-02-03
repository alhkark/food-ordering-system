package com.food.ordering.system.kafka.producer.service.impl;

import com.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase>
    implements KafkaProducer<K, V> {

  private final KafkaTemplate<K, V> kafkaTemplate;

  @Override
  public void send(String topicName, K key, V message, Consumer<SendResult<K, V>> callback) {
    log.info("Sending record to topic: {}, key: {}, message: {}", topicName, key, message);
    var kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
    kafkaResultFuture.whenComplete(
        (result, e) -> {
          if (e != null) {
            log.error(
                "Error on kafka producer with topic: {}, key: {}, message: {}, exception: {}",
                topicName,
                key,
                message,
                e.getMessage());
            throw new KafkaProducerException(
                "Error on kafka producer with key: " + key + " and message: " + message);
          }
          callback.accept(result);
        });
  }

  @PreDestroy
  public void close() {
    Optional.ofNullable(kafkaTemplate).ifPresent(KafkaTemplate::destroy);
  }
}
