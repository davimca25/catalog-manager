package com.example.batchservice.config;

import com.example.batchservice.dto.OrderEventDTO;
import com.example.batchservice.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.stock.name}")
    private String stockExchangeName;

    @Value("${rabbitmq.routing.stock.key}")
    private String stockRoutingKey;

    @Bean
    public ItemReader<OrderEventDTO> orderItemReader(@Value("${jobParameters['orderId']}") String orderId) {
        OrderEventDTO orderEvent = fetchOrderData(orderId);

        return new ListItemReader<>(List.of(orderEvent));
    }

    @Bean
    public ItemProcessor<OrderEventDTO, OrderEventDTO> orderItemProcessor() {
        return event -> {
            if (event.status() == Status.FAILED) {
                return null;
            }

            BigDecimal totalAmount = calculateTotal(event.items());

            log.info("Processing order ID: {}. Total amount: R$ {}", event.orderId(), totalAmount);

            return event;
        };
    }

    @Bean
    public ItemWriter<OrderEventDTO> orderItemWriter() {
        return items -> {
            for (OrderEventDTO event : items) {
                OrderEventDTO completedEvent = new OrderEventDTO(
                        event.orderId(),
                        event.userName(),
                        Status.COMPLETED,
                        event.createdAt(),
                        event.items()
                );

                log.info("Finished batch process for order ID: {}. Sending notification to stock.queue", completedEvent.orderId());

                rabbitTemplate.convertAndSend(stockExchangeName, stockRoutingKey, completedEvent);
            }
        };
    }

    @Bean
    public Step processOrderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("processOrderStep", jobRepository)
                .<OrderEventDTO, OrderEventDTO>chunk(10)
                .transactionManager(transactionManager)
                .reader(orderItemReader())
                .processor(orderItemProcessor())
                .writer(orderItemWriter())
                .build();
    }

    @Bean
    public Job processOrderJob(JobRepository jobRepository, Step processOrderStep) {
        return new JobBuilder("processOrderJob", jobRepository)
                .start(processOrderStep)
                .build();
    }

    private BigDecimal calculateTotal(List<OrderEventDTO.OrderItemEventDTO> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}