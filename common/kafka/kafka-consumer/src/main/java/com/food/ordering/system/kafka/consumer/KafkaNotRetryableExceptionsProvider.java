package com.food.ordering.system.kafka.consumer;

public interface KafkaNotRetryableExceptionsProvider {
  Class<? extends Exception>[] getExceptions();
}
