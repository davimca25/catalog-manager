package com.example.orderproducerservice.config;

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

    // order response

    @Value("${rabbitmq.queue.order.response.name:order.response.queue}")
    private String orderResponseQueueName;

    @Value("${rabbitmq.exchange.order.response.name:order.response.exchange}")
    private String orderResponseExchangeName;

    @Value("${rabbitmq.routing.order.response.key:order.response.routing.key}")
    private String orderResponseRoutingKey;

    // product stock sync

    @Value("${rabbitmq.queue.product.stock.name:product.stock.queue}")
    private String productStockQueueName;

    @Value("${rabbitmq.exchange.product.stock.name:product.stock.exchange}")
    private String productStockExchangeName;

    @Value("${rabbitmq.routing.product.stock.key:product.stock.routing.key}")
    private String productStockRoutingKey;

    // beans for order response listener

    @Bean
    public Queue orderResponseQueue() {
        return new Queue(orderResponseQueueName, true);
    }

    @Bean
    public TopicExchange orderResponseExchange() {
        return new TopicExchange(orderResponseExchangeName);
    }

    @Bean
    public Binding orderResponseBinding(Queue orderResponseQueue, TopicExchange orderResponseExchange) {
        return BindingBuilder
                .bind(orderResponseQueue)
                .to(orderResponseExchange)
                .with(orderResponseRoutingKey);
    }

    // beans for product stock listener

    @Bean
    public Queue productStockQueue() {
        return new Queue(productStockQueueName, true);
    }

    @Bean
    public TopicExchange productStockExchange() {
        return new TopicExchange(productStockExchangeName);
    }

    @Bean
    public Binding productStockBinding(Queue productStockQueue, TopicExchange productStockExchange) {
        return BindingBuilder
                .bind(productStockQueue)
                .to(productStockExchange)
                .with(productStockRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}

