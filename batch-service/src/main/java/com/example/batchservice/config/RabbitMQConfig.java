package com.example.batchservice.config;

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

    @Value("${rabbitmq.queue.order.created.name:order.created.queue}")
    private String orderCreatedQueueName;

    @Value("${rabbitmq.exchange.order.created.name:order.created.exchange}")
    private String orderCreatedExchangeName;

    @Value("${rabbitmq.routing.order.created.key:order.created.routing.key}")
    private String orderCreatedRoutingKey;

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueueName, true);
    }

    @Bean
    public TopicExchange orderCreatedExchange() {
        return new TopicExchange(orderCreatedExchangeName);
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderCreatedExchange) {
        return BindingBuilder
                .bind(orderCreatedQueue)
                .to(orderCreatedExchange)
                .with(orderCreatedRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
