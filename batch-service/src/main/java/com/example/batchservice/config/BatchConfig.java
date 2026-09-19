package com.example.batchservice.config;

import com.example.batchservice.dto.OrderEventDTO;
import com.example.batchservice.model.OrderBatchStaging;
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
import org.springframework.batch.infrastructure.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.stock.name:stock.exchange}")
    private String stockExchangeName;

    @Value("${rabbitmq.routing.stock.key:stock.routing.key}")
    private String stockRoutingKey;

    @Bean
    public ItemReader<OrderBatchStaging> orderItemReader(MongoTemplate mongoTemplate) {

        Query query = new Query(Criteria.where("status").is(Status.PENDING));

        return new MongoPagingItemReaderBuilder<OrderBatchStaging>()
                .name("orderItemReader")
                .template(mongoTemplate)
                .collection("order_staging")
                .targetType(OrderBatchStaging.class)
                .query(query)
                .pageSize(10)
                .sorts(Map.of("createdAt", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<OrderBatchStaging, OrderEventDTO> orderItemProcessor() {
        return staging -> {
            if (staging.getStatus() == Status.FAILED) {
                return null;
            }

            BigDecimal totalAmount = calculateTotal(staging.getItems());

            log.info("Processing order ID: {}. Total amount: R$ {}", staging.getOrderId(), totalAmount);

            return new OrderEventDTO(
                    staging.getOrderId(),
                    staging.getUserName(),
                    staging.getStatus(),
                    staging.getCreatedAt(),
                    staging.getItems()
            );
        };
    }

        @Bean
        public ItemWriter<OrderEventDTO> orderItemWriter(MongoTemplate mongoTemplate) {
            return items -> {

                List<UUID> completedOrderIds = new ArrayList<>();

                for (OrderEventDTO event : items) {
                    OrderEventDTO completedEvent = new OrderEventDTO(
                            event.orderId(),
                            event.userName(),
                            Status.PROCESSING,
                            event.createdAt(),
                            event.items()
                    );

                    completedOrderIds.add(completedEvent.orderId());

                    log.info("Finished batch process for order ID: {}. Sending notification to stock.queue", completedEvent.orderId());

                    rabbitTemplate.convertAndSend(stockExchangeName, stockRoutingKey, completedEvent);
                }

                if (!completedOrderIds.isEmpty()) {
                    Query query = new Query(Criteria.where("_id").in(completedOrderIds));
                    Update update = new Update().set("status", Status.PROCESSING);

                    mongoTemplate.updateMulti(query, update, OrderBatchStaging.class);
                }
            };
        }

    @Bean
    public Step processOrderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, MongoTemplate mongoTemplate) {
        return new StepBuilder("processOrderStep", jobRepository)
                .<OrderBatchStaging, OrderEventDTO>chunk(10)
                .transactionManager(transactionManager)
                .reader(orderItemReader(mongoTemplate))
                .processor(orderItemProcessor())
                .writer(orderItemWriter(mongoTemplate))
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