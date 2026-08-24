package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.ports.output.ai.order.noteinterpreter.OrderNoteInterpreter;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = OrderTestConfiguration.class)
public class BaseTest {

  @MockitoBean PaymentRequestMessagePublisher paymentRequestMessagePublisher;

  @MockitoBean RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher;

  @MockitoBean OrderRepository orderRepository;

  @MockitoBean CustomerRepository customerRepository;

  @MockitoBean RestaurantRepository restaurantRepository;

  @MockitoBean PaymentOutboxRepository paymentOutboxRepository;

  @MockitoBean ApprovalOutboxRepository approvalOutboxRepository;

  @MockitoBean ArchivePaymentOutboxRepository archivePaymentOutboxRepository;

  @MockitoBean ArchiveApprovalOutboxRepository archiveApprovalOutboxRepository;

  @MockitoBean OrderNoteInterpreter orderNoteInterpreter;
}
