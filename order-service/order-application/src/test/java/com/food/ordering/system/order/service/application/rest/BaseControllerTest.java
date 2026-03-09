package com.food.ordering.system.order.service.application.rest;

import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest
@AutoConfigureRestTestClient
public class BaseControllerTest {

  @Autowired RestTestClient restTestClient;

  @MockitoBean OrderApplicationService orderApplicationService;
}
