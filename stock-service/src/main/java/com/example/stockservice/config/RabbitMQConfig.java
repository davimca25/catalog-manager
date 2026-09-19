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

    @Value("${rabbitmq.queue.stock.name:stock.queue}")
    private String stockQueueName;

    @Value("${rabbitmq.exchange.stock.name:stock.exchange}")
    private String stockExchangeName;

    @Value("${rabbitmq.routing.stock.key:stock.routing.key}")
    private String stockRoutingKey;

    // product event from order-service

    @Value("${rabbitmq.queue.product.name:product.queue}")
    private String productQueueName;

    @Value("${rabbitmq.exchange.product.name:product.exchange}")
    private String productExchangeName;

    @Value("${rabbitmq.routing.product.key:product.routing.key}")
    private String productRoutingKey;

    @Bean
    public Queue stockQueue() {
        return new Queue(stockQueueName, true);
    }

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(stockExchangeName);
    }

    @Bean
    public Binding stockBinding(
            Queue stockQueue,
            TopicExchange stockExchange
    ) {
        return BindingBuilder
                .bind(stockQueue)
                .to(stockExchange)
                .with(stockRoutingKey);
    }

    @Bean
    public Queue productQueue() {
        return new Queue(productQueueName, true);
    }

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(productExchangeName);
    }

    @Bean
    public Binding productBinding(
            Queue productQueue,
            TopicExchange productExchange
    ) {
        return BindingBuilder
                .bind(productQueue)
                .to(productExchange)
                .with(productRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
