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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

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

        List<OrderItemRequestDTO> orderItems = savedOrder.getItems().stream()
                .map(item -> new OrderItemRequestDTO(
                        item.getProduct().getId(),
                        item.getQuantity()
                ))
                .toList();


        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt(),
                orderItems
        );
    }

    public void listUserOrders(UUID userId) {

    }
}
