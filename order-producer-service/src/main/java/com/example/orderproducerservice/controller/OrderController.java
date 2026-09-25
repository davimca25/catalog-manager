package com.example.orderproducerservice.controller;

import com.example.orderproducerservice.dto.OrderRequestDTO;
import com.example.orderproducerservice.dto.OrderResponseDTO;
import com.example.orderproducerservice.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequestDTO, userName));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> listUserOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        return ResponseEntity.ok().body(orderService.listUserOrders(userName));
    }
}
