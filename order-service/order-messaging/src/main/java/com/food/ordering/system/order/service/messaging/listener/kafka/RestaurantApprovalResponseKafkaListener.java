package com.food.ordering.system.order.service.messaging.listener.kafka;

import static com.food.ordering.system.domain.DomainConstants.FAILURE_MESSAGE_DELIMITER;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.food.ordering.system.kafka.producer.service.KafkaMessageHelper;
import com.food.ordering.system.messaging.DebeziumOp;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.outbox.payload.RestaurantOrderEventPayload;
import debezium_restaurant_order_outbox.restaurant.order_outbox.Envelope;
import debezium_restaurant_order_outbox.restaurant.order_outbox.Value;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<Envelope> {

  private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  @KafkaListener(
      id = "restaurant-approval-response-listener",
      groupId = "${kafka-consumer-config.restaurant-approval-response-consumer-group-id}",
      topics = "${order-service.restaurant-approval-response-topic-name}")
  public void receive(
      @Payload List<Envelope> messages,
      @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
      @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info(
        "{} number of restaurant approval responses received!",
        messages.stream()
            .filter(
                message ->
                    message.getBefore() == null
                        && DebeziumOp.CREATE.getValue().equals(message.getOp()))
            .toList()
            .size());
    messages.forEach(
        avroModel -> {
          if (avroModel.getBefore() == null
              && DebeziumOp.CREATE.getValue().equals(avroModel.getOp())) {
            log.info("Incoming message in RestaurantApprovalResponseKafkaListener: {}", avroModel);
            Value restaurantApprovalResponseAvroModel = avroModel.getAfter();
            RestaurantOrderEventPayload restaurantOrderEventPayload =
                kafkaMessageHelper.getOrderEventPayload(
                    restaurantApprovalResponseAvroModel.getPayload(),
                    RestaurantOrderEventPayload.class);
            try {
              if (OrderApprovalStatus.APPROVED
                  .name()
                  .equals(restaurantOrderEventPayload.orderApprovalStatus())) {
                log.info(
                    "Processing approved order for order id : {}",
                    restaurantOrderEventPayload.orderId());
                restaurantApprovalResponseMessageListener.orderApproved(
                    orderMessagingDataMapper.toApprovalResponse(
                        restaurantOrderEventPayload, restaurantApprovalResponseAvroModel));
              } else if (OrderApprovalStatus.REJECTED
                  .name()
                  .equals(restaurantOrderEventPayload.orderApprovalStatus())) {
                log.info(
                    "Processing rejected order for order id: {}, with failure messages: {}",
                    restaurantOrderEventPayload.orderId(),
                    String.join(
                        FAILURE_MESSAGE_DELIMITER, restaurantOrderEventPayload.failureMessages()));
                restaurantApprovalResponseMessageListener.orderRejected(
                    orderMessagingDataMapper.toApprovalResponse(
                        restaurantOrderEventPayload, restaurantApprovalResponseAvroModel));
              }
            } catch (OptimisticLockingFailureException e) {
              log.error(
                  "Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                  restaurantOrderEventPayload.orderId());
            } catch (OrderNotFoundException e) {
              log.error("No order found for order id: {}", restaurantOrderEventPayload.orderId());
            } catch (DataAccessException e) {
              SQLException sqlException = (SQLException) e.getRootCause();
              if (sqlException != null
                  && sqlException.getSQLState() != null
                  && PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                // NO-OP for unique constraint exception
                log.error(
                    "Caught unique constraint exception with sql state: {} "
                        + "in RestaurantApprovalResponseKafkaListener for order id: {}",
                    sqlException.getSQLState(),
                    restaurantOrderEventPayload.orderId());
              }
            }
          }
        });
  }

  @KafkaListener(
      id = "restaurant-approval-response-listener-dlt",
      topics = "${order-service.restaurant-approval-response-topic-name}.DLT",
      groupId = "${kafka-consumer-config.restaurant-approval-response-dlt-consumer-group-id}")
  public void handleDlt(
      @Payload List<byte[]> messages,
      @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics,
      @Header(name = KafkaHeaders.EXCEPTION_MESSAGE, required = false) List<String> errors) {
    log.error("Poison DLT batch size={}, errors={}", messages.size(), errors);
  }
}
