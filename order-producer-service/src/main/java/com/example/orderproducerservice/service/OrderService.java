package com.example.orderproducerservice.service;

import com.example.orderproducerservice.dto.OrderEventDTO;
import com.example.orderproducerservice.dto.OrderItemRequestDTO;
import com.example.orderproducerservice.dto.OrderRequestDTO;
import com.example.orderproducerservice.dto.OrderResponseDTO;
import com.example.orderproducerservice.exception.ResourceNotFoundException;
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

    @Value("${rabbitmq.exchange.order.created.name:order.created.exchange}")
    private String OrderCreatedExchangeName;

    @Value("${rabbitmq.routing.order.created.key:order.created.routing.key}")
    private String OrderCreatedRoutingKey;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, String userName) {
        Order order = new Order();

        for (OrderItemRequestDTO request : orderRequestDTO.items()) {
            Product product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

            if (!product.isActive()) {
                throw new ResourceNotFoundException("Product: " + product.getName() + " not available.");
            }

            OrderItem orderItem = new OrderItem(product, request.quantity());

            order.addItem(orderItem);
        }
        order.setUserName(userName);
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
                savedOrder.getUserName(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt(),
                orderItemsDTO
        );

        List<OrderEventDTO.OrderItemEventDTO> orderItemsEventDTO = savedOrder.getItems().stream()
                .map(item -> new OrderEventDTO.OrderItemEventDTO(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        OrderEventDTO orderEventDTO = new OrderEventDTO(
                savedOrder.getId(),
                savedOrder.getUserName(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt(),
                orderItemsEventDTO
        );

        rabbitTemplate.convertAndSend(OrderCreatedExchangeName, OrderCreatedRoutingKey, orderEventDTO);

        return responseDTO;
    }

    public List<OrderResponseDTO> listUserOrders(String userName) {
        List<Order> orders = orderRepository.findByUserName(userName);

        return orders.stream().map(order -> {
            List<OrderItemRequestDTO> orderItemDTOS = order.getItems().stream().map(item -> new OrderItemRequestDTO(
                    item.getProduct().getId(),
                    item.getQuantity()
            )).toList();

            return new OrderResponseDTO(
                    order.getId(),
                    order.getUserName(),
                    order.getStatus(),
                    order.getCreatedAt(),
                    orderItemDTOS
            );
        }).toList();
    }

    public void deleteOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        orderRepository.delete(order);
    }
}
