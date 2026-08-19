package com.food.ordering.system.kafka.consumer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.food.ordering.system.kafka.config.data.KafkaConfigData;
import com.food.ordering.system.kafka.config.data.KafkaConsumerConfigData;
import java.io.Serializable;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@RequiredArgsConstructor
@EnableKafka
@Configuration
public class KafkaConsumerConfig<K extends Serializable, V extends SpecificRecordBase> {

  private final KafkaConfigData kafkaConfigData;
  private final KafkaConsumerConfigData kafkaConsumerConfigData;
  private final KafkaTemplate<K, V> kafkaTemplate;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new java.util.HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(
        ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
        kafkaConsumerConfigData.getKeyDeserializer());
    props.put(
        ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
        kafkaConsumerConfigData.getValueDeserializer());
    props.put(
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerConfigData.getAutoOffsetReset());
    props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
    props.put(
        kafkaConsumerConfigData.getSpecificAvroReaderKey(),
        kafkaConsumerConfigData.getSpecificAvroReader());
    props.put(
        ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerConfigData.getSessionTimeoutMs());
    props.put(
        ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
        kafkaConsumerConfigData.getHeartbeatIntervalMs());
    props.put(
        ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerConfigData.getMaxPollIntervalMs());
    props.put(
        ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
        kafkaConsumerConfigData.getMaxPartitionFetchBytesDefault()
            * kafkaConsumerConfigData.getMaxPartitionFetchBytesBoostFactor());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerConfigData.getMaxPollRecords());
    return props;
  }

  @Bean
  public ConsumerFactory<K, V> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<K, V>>
      kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<K, V> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setBatchListener(kafkaConsumerConfigData.getBatchListener());
    factory.setConcurrency(kafkaConsumerConfigData.getConcurrencyLevel());
    factory.setAutoStartup(kafkaConsumerConfigData.getAutoStartup());
    factory.getContainerProperties().setPollTimeout(kafkaConsumerConfigData.getPollTimeoutMs());

    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
    factory.setCommonErrorHandler(kafkaErrorHandler());

    return factory;
  }

  @Bean
  public CommonErrorHandler kafkaErrorHandler() {
    FixedBackOff backOff =
        new FixedBackOff(
            kafkaConsumerConfigData.getRetryIntervalMs(),
            kafkaConsumerConfigData.getRetryMaxAttempts());
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (record, ex) -> {
              if (record.topic().endsWith(kafkaConsumerConfigData.getDltSuffix())) {
                return null;
              }
              return new TopicPartition(
                  record.topic() + kafkaConsumerConfigData.getDltSuffix(), -1);
            });
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
    errorHandler.addNotRetryableExceptions(
        IllegalArgumentException.class,
        JsonProcessingException.class,
        SerializationException.class);
    return errorHandler;
  }
}
