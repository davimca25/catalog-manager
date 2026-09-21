package com.example.batchservice.listener;

import com.example.batchservice.dto.OrderEventDTO;
import com.example.batchservice.model.OrderBatchStaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumerListener {

    private final JobOperator jobOperator;
    private final Job processOrderJob;
    private final MongoTemplate mongoTemplate;

    @RabbitListener(queues = "${rabbitmq.queue.order.created.name:order.created.queue}")
    public void receiveOrderEvent(OrderEventDTO orderEventDTO) {
        log.info("Message received from order.queue: Order ID = {}, UserName = {}", orderEventDTO.orderId(), orderEventDTO.userName());

        try {
            OrderBatchStaging orderBatchStaging = new OrderBatchStaging(
                    orderEventDTO.orderId(),
                    orderEventDTO.userName(),
                    orderEventDTO.status(),
                    orderEventDTO.createdAt(),
                    orderEventDTO.items()
            );

            mongoTemplate.save(orderBatchStaging);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("orderId", orderEventDTO.orderId().toString())
                    .addLocalDateTime("time", LocalDateTime.now())
                    .toJobParameters();

            jobOperator.start(processOrderJob, jobParameters);
        } catch (Exception e) {
            log.error("Job error for order ID = {}", orderEventDTO.orderId(), e);
        }
    }
}
