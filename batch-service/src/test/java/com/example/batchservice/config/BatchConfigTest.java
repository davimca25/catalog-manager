package com.example.batchservice.config;

import com.example.batchservice.dto.OrderEventDTO;
import com.example.batchservice.model.OrderBatchStaging;
import com.example.batchservice.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBatchTest
@SpringBootTest
@TestPropertySource(properties = {
        "rabbitmq.exchange.stock.name=stock.exchange.test",
        "rabbitmq.queue.stock.name=stock.queue.test",
        "rabbitmq.routing.stock.key=stock.routing.key.test",
        "rabbitmq.queue.order.name=order.queue.test"
})
class BatchConfigTest {

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public JobOperatorTestUtils batchJobOperatorTestUtils(
                Job processOrderJob,
                JobRepository jobRepository,
                JobOperator jobOperator) {

            JobOperatorTestUtils utils =
                    new JobOperatorTestUtils(jobOperator, jobRepository);

            utils.setJob(processOrderJob);
            utils.setJobRepository(jobRepository);
            utils.setJobOperator(jobOperator);

            return utils;
        }
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("order_staging");
    }

    @Test
    void shouldExecuteFullJobSuccessfully() throws Exception {

        UUID orderId = UUID.randomUUID();
        String userName = "joao";

        OrderBatchStaging stagingOrder = new OrderBatchStaging(
                orderId,
                userName,
                Status.PENDING,
                LocalDateTime.now(),
                List.of(
                        new OrderEventDTO.OrderItemEventDTO(
                                UUID.randomUUID(),
                                2,
                                new BigDecimal("49.90")
                        )
                )
        );

        mongoTemplate.save(stagingOrder, "order_staging");

        JobParameters parameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("orderId", orderId.toString())
                .toJobParameters();

        JobExecution jobExecution =
                jobOperatorTestUtils.startJob(parameters);

        assertEquals(
                BatchStatus.COMPLETED,
                jobExecution.getStatus()
        );

        verify(rabbitTemplate, atLeastOnce())
                .convertAndSend(
                        any(),
                        any(),
                        any(OrderEventDTO.class)
                );

        OrderBatchStaging updatedDoc = mongoTemplate.findOne(
                new Query(Criteria.where("_id").is(orderId)),
                OrderBatchStaging.class,
                "order_staging"
        );

        assertNotNull(updatedDoc);
        assertEquals(Status.COMPLETED, updatedDoc.getStatus());
    }
}
