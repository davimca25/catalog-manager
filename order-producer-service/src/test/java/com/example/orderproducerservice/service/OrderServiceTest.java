package com.example.orderproducerservice.service;

import com.example.orderproducerservice.dto.OrderItemRequestDTO;
import com.example.orderproducerservice.dto.OrderRequestDTO;
import com.example.orderproducerservice.dto.OrderResponseDTO;
import com.example.orderproducerservice.model.Order;
import com.example.orderproducerservice.model.OrderItem;
import com.example.orderproducerservice.model.Product;
import com.example.orderproducerservice.model.Status;
import com.example.orderproducerservice.repository.OrderRepository;
import com.example.orderproducerservice.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create a order")
    void shouldCreateAOrder() {

        // arrange

        UUID productId = UUID.randomUUID();
        BigDecimal price = new BigDecimal(132.99);
        Product product = new Product(productId, "Livro", price, 12);
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO(productId, 5);

        List<OrderItemRequestDTO> orderItemRequestDTOList = new ArrayList<>();
        orderItemRequestDTOList.add(orderItemRequestDTO);

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(orderItemRequestDTOList);

        UUID userId = UUID.randomUUID();
        UUID orderItemId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        OrderItem orderItem = new OrderItem(product, 5);
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        Order order = new Order(orderId, userId, Status.PENDING, LocalDateTime.now(), orderItemList);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // act

        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderRequestDTO, userId);

        // assert

        assertNotNull(orderResponseDTO);
        assertEquals(userId, orderResponseDTO.userId());
        assertEquals(Status.PENDING, orderResponseDTO.status());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).findById(productId);

    }

    @Test
    void ShouldlistUserOrders() {

        // arrange

        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BigDecimal price = new BigDecimal(132.99);

        Product product = new Product(productId, "Livro", price, 12);
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO(productId, 5);
        List<OrderItemRequestDTO> orderItemRequestDTOList = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItemList.add(orderItem);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(orderItemRequestDTOList);
        Order order = new Order(orderId, userId, Status.PENDING, LocalDateTime.now(), orderItemList);

        orderItemRequestDTOList.add(orderItemRequestDTO);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));

        // act
        List<OrderResponseDTO> orderResponseDTOList = orderService.listUserOrders(userId);

        // assert

        assertNotNull(orderResponseDTOList);
        verify(orderRepository, times(1)).findByUserId(userId);
    }

    @Test
    void ShoulddeleteOrder() {

        //arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        OrderItem orderItem = new OrderItem();
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        Order order = new Order(orderId, userId, Status.PENDING, LocalDateTime.now(), orderItemList);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //act

        orderService.deleteOrder(orderId);

        //assert

        verify(orderRepository, times(1)).delete(order);


    }
}