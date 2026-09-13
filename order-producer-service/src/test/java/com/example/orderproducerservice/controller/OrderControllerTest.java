package com.example.orderproducerservice.controller;

import com.example.orderproducerservice.dto.OrderItemRequestDTO;
import com.example.orderproducerservice.dto.OrderRequestDTO;
import com.example.orderproducerservice.dto.OrderResponseDTO;
import com.example.orderproducerservice.model.Status;
import com.example.orderproducerservice.service.JwtService;
import com.example.orderproducerservice.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    private final String MOCK_USER_ID = "21275859-1801-4632-b71b-0067589ce6ac";

    @Test
    @WithMockUser(username = MOCK_USER_ID)
    @DisplayName("Should create an order and return 201 Created")
    void createOrderAndReturnCreated() throws Exception {

        // arrange

        String userName = "davi";

        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO(productId, 5);
        List<OrderItemRequestDTO> orderItemRequestDTOList = new ArrayList<>();

        orderItemRequestDTOList.add(orderItemRequestDTO);

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(orderItemRequestDTOList);
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(orderId, userName, Status.PENDING, LocalDateTime.now(), orderItemRequestDTOList);

        when(orderService.createOrder(orderRequestDTO, userName)).thenReturn(orderResponseDTO);

        // act

        String jsonRequest = objectMapper.writeValueAsString(orderRequestDTO);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());

        // assert
    }

    @Test
    void listUserOrders() {
    }
}