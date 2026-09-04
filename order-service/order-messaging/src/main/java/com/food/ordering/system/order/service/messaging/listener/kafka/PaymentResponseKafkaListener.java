package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.kafka.producer.service.KafkaMessageHelper;
import com.food.ordering.system.messaging.DebeziumOp;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.outbox.payload.PaymentOrderEventPayload;
import debezium_payment_order_outbox.payment.order_outbox.Envelope;
import debezium_payment_order_outbox.payment.order_outbox.Value;
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
@RequiredArgsConstructor
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<Envelope> {

  private final PaymentResponseMessageListener paymentResponseMessageListener;
  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  @KafkaListener(
      id = "payment-response-listener",
      groupId = "${kafka-consumer-config.payment-response-consumer-group-id}",
      topics = "${order-service.payment-response-topic-name}")
  public void receive(
      @Payload List<Envelope> messages,
      @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
      @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
      @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info(
        "{} number of payment responses received!",
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
            log.info("Incoming message in PaymentResponseKafkaListener: {}", avroModel);
            Value paymentResponseAvroModel = avroModel.getAfter();
            PaymentOrderEventPayload paymentOrderEventPayload =
                kafkaMessageHelper.getOrderEventPayload(
                    paymentResponseAvroModel.getPayload(), PaymentOrderEventPayload.class);
            try {
              if (PaymentStatus.COMPLETED.name().equals(paymentOrderEventPayload.paymentStatus())) {
                log.info(
                    "Processing successful payment for order id : {}",
                    paymentOrderEventPayload.orderId());
                paymentResponseMessageListener.paymentCompleted(
                    orderMessagingDataMapper.toPaymentResponse(
                        paymentOrderEventPayload, paymentResponseAvroModel));
              } else if (PaymentStatus.CANCELLED
                      .name()
                      .equals(paymentOrderEventPayload.paymentStatus())
                  || PaymentStatus.FAILED.name().equals(paymentOrderEventPayload.paymentStatus())) {
                log.info(
                    "Processing unsuccessful payment for order id : {}",
                    paymentOrderEventPayload.orderId());
                paymentResponseMessageListener.paymentCancelled(
                    orderMessagingDataMapper.toPaymentResponse(
                        paymentOrderEventPayload, paymentResponseAvroModel));
              }
            } catch (OptimisticLockingFailureException e) {
              log.error(
                  "Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                  paymentOrderEventPayload.orderId());
            } catch (OrderNotFoundException e) {
              log.error("No order found for order id: {}", paymentOrderEventPayload.orderId());
            } catch (DataAccessException e) {
              SQLException sqlException = (SQLException) e.getRootCause();
              if (sqlException != null
                  && sqlException.getSQLState() != null
                  && PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                // NO-OP for unique constraint exception
                log.error(
                    "Caught unique constraint exception with sql state: {} "
                        + "in PaymentResponseKafkaListener for order id: {}",
                    sqlException.getSQLState(),
                    paymentOrderEventPayload.orderId());
              }
            }
          }
        });
  }

  @KafkaListener(
      id = "payment-response-listener-dlt",
      topics = "${order-service.payment-response-topic-name}.DLT",
      groupId = "${kafka-consumer-config.payment-response-dlt-consumer-group-id}")
  public void handleDlt(
      @Payload List<byte[]> messages,
      @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics,
      @Header(name = KafkaHeaders.EXCEPTION_MESSAGE, required = false) List<String> errors) {
    log.error("Poison DLT batch size={}, errors={}", messages.size(), errors);
  }
}
