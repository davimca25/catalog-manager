package com.example.stockservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    // batch-service

    @Value("${rabbitmq.queue.stock.decrement.name:stock.decrement.queue}")
    private String stockDecrementQueueName;

    @Value("${rabbitmq.exchange.stock.decrement.name:stock.decrement.exchange}")
    private String stockDecrementExchangeName;

    @Value("${rabbitmq.routing.stock.decrement.key:stock.decrement.routing.key}")
    private String stockDecrementRoutingKey;

    // product event from order-service

    @Value("${rabbitmq.queue.product.created.name:product.created.queue}")
    private String productCreatedQueueName;

    @Value("${rabbitmq.exchange.product.created.name:product.created.exchange}")
    private String productCreatedExchangeName;

    @Value("${rabbitmq.routing.product.created.key:product.created.routing.key}")
    private String productCreatedRoutingKey;

    @Bean
    public Queue stockDecrementQueue() {
        return new Queue(stockDecrementQueueName, true);
    }

    @Bean
    public TopicExchange stockDecrementExchange() {
        return new TopicExchange(stockDecrementExchangeName);
    }

    @Bean
    public Binding stockDecrementBinding(
            Queue stockDecrementQueue,
            TopicExchange stockDecrementExchange
    ) {
        return BindingBuilder
                .bind(stockDecrementQueue)
                .to(stockDecrementExchange)
                .with(stockDecrementRoutingKey);
    }

    @Bean
    public Queue productCreatedQueue() {
        return new Queue(productCreatedQueueName, true);
    }

    @Bean
    public TopicExchange productCreatedExchange() {
        return new TopicExchange(productCreatedExchangeName);
    }

    @Bean
    public Binding productCreatedBinding(
            Queue productCreatedQueue,
            TopicExchange productCreatedExchange
    ) {
        return BindingBuilder
                .bind(productCreatedQueue)
                .to(productCreatedExchange)
                .with(productCreatedRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
