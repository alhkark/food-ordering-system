package com.food.ordering.system.customer.service.domain.outbox.scheduler;

import static com.food.ordering.system.domain.DomainConstants.UTC;

import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerEventPayload;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.repository.ArchiveCustomerOutboxRepository;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerOutboxHelper {

  private final CustomerOutboxRepository customerOutboxRepository;
  private final ArchiveCustomerOutboxRepository archiveCustomerOutboxRepository;
  private final JsonMapper jsonMapper;

  @Transactional(readOnly = true)
  public List<CustomerOutboxMessage> getCustomerOutboxMessageByOutboxStatus(
      OutboxStatus outboxStatus) {
    return customerOutboxRepository.findByOutboxStatus(outboxStatus);
  }

  @Transactional
  public void save(CustomerOutboxMessage customerOutboxMessage) {
    CustomerOutboxMessage response = customerOutboxRepository.save(customerOutboxMessage);
    if (response == null) {
      log.error(
          "Could not save CustomerOutboxMessage with outbox id: {}", customerOutboxMessage.getId());
      throw new CustomerDomainException(
          "Could not save CustomerOutboxMessage with outbox id: " + customerOutboxMessage.getId());
    }
    log.info("CustomerOutboxMessage saved with outbox id: {}", customerOutboxMessage.getId());
  }

  @Transactional
  public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
    customerOutboxRepository.deleteByOutboxStatus(outboxStatus);
  }

  @Transactional
  public void archiveApprovalOutboxRepository(List<CustomerOutboxMessage> customerOutboxMessages) {
    archiveCustomerOutboxRepository.saveAll(customerOutboxMessages);
  }

  @Transactional
  public void saveCustomerOutboxMessage(
      CustomerEventPayload customerEventPayload,
      OutboxStatus outboxStatus,
      ZonedDateTime createdAt) {
    save(
        CustomerOutboxMessage.builder()
            .id(UUID.randomUUID())
            .customerId(customerEventPayload.customerId())
            .outboxStatus(outboxStatus)
            .payload(createPayload(customerEventPayload))
            .createdAt(createdAt)
            .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
            .build());
  }

  private String createPayload(CustomerEventPayload customerEventPayload) {
    try {
      return jsonMapper.writeValueAsString(customerEventPayload);
    } catch (JacksonException e) {
      log.error(
          "Could not create CustomerEventPayload object for customer id: {}",
          customerEventPayload.customerId(),
          e);
      throw new CustomerDomainException(
          "Could not create CustomerEventPayload object for order id: "
              + customerEventPayload.customerId(),
          e);
    }
  }
}
