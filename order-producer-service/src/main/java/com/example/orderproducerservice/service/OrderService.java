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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:order.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:order.routing.key}")
    private String routingKey;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, UUID userID) {
        Order order = new Order();

        for (OrderItemRequestDTO request : orderRequestDTO.items()) {
            Product product = productRepository.findById(request.productId()).orElseThrow(() -> new RuntimeException("Product not found."));

            OrderItem orderItem = new OrderItem(product, request.quantity());

            order.addItem(orderItem);
        }
        order.setUserId(userID);
        order.setStatus(Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder= orderRepository.save(order);

        List<OrderItemRequestDTO> orderItemsDTO = savedOrder.getItems().stream()
                .map(item -> new OrderItemRequestDTO(
                        item.getProduct().getId(),
                        item.getQuantity()
                ))
                .toList();

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt(),
                orderItemsDTO
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, responseDTO);

        return responseDTO;
    }

    public List<OrderResponseDTO> listUserOrders(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(order -> {
            List<OrderItemRequestDTO> orderItemDTOS = order.getItems().stream().map(item -> new OrderItemRequestDTO(
                    item.getProduct().getId(),
                    item.getQuantity()
            )).toList();

            return new OrderResponseDTO(
                    order.getId(),
                    order.getUserId(),
                    order.getStatus(),
                    order.getCreatedAt(),
                    orderItemDTOS
            );
        }).toList();
    }

    public void deleteOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        orderRepository.delete(order);
    }
}
