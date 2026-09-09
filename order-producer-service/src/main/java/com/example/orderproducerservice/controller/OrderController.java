package com.example.orderproducerservice.controller;

import com.example.orderproducerservice.dto.OrderRequestDTO;
import com.example.orderproducerservice.dto.OrderResponseDTO;
import com.example.orderproducerservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;


    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());


        rabbitTemplate.convertAndSend("Order created:" + orderRequestDTO.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequestDTO, userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> listUserOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity.ok().body(orderService.listUserOrders(userId));
    }
}
