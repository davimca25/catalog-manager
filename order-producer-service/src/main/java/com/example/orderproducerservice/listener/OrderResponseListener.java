package com.example.orderproducerservice.listener;

import com.example.orderproducerservice.dto.OrderEventDTO;
import com.example.orderproducerservice.model.Status;
import com.example.orderproducerservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderResponseListener {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = "${rabbitmq.queue.order.response.name:order.response.queue}")
    @Transactional
    public void receiveOrderResponseEvent(OrderEventDTO orderEventDTO) {

        log.info("Message received from order response queue: OrderId = {}, Status = {}",
                orderEventDTO.orderId(),
                orderEventDTO.status()
        );

        orderRepository.findById(orderEventDTO.orderId()).ifPresentOrElse(order -> {

            if (order.getStatus() == Status.COMPLETED || order.getStatus() == Status.FAILED) {

                log.warn("Order {} already finished, ({}). Ignoring duplicate event",
                        order.getId(),
                        order.getStatus()
                );
                return;
            }

            order.setStatus(orderEventDTO.status());
            orderRepository.save(order);

            log.info("Order status updated successfully in DB: OrderId = {}, New Status = {}",
                    order.getId(),
                    order.getStatus()
            );

        }, () -> log.error("Order not found in order-producer-service: OrderId = {}", orderEventDTO.orderId()));

    }
}
