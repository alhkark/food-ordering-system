package com.food.ordering.system.kafka.producer.service;

import java.io.Serializable;
import java.util.function.Consumer;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.support.SendResult;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
  void send(String topicName, K key, V message, Consumer<SendResult<K, V>> callback);
}
