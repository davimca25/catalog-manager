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

    @Value("${rabbitmq.queue.stock.name:stock.queue}")
    private String stockQueueName;

    @Value("${rabbitmq.exchange.stock.name:stock.exchange}")
    private String stockExchangeName;

    @Value("${rabbitmq.routing.stock.key:stock.routing.key}")
    private String stockRoutingKey;

    @Value("${rabbitmq.queue.order.response.name:order.response.queue}")
    private String orderResponseQueueName;

    @Value("${rabbitmq.exchange.order.response.name:order.response.exchange}")
    private String orderResponseExchangeName;

    @Value("${rabbitmq.routing.order.response.key:order.response.routing.key}")
    private String orderResponseRoutingKey;

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
    public Queue orderResponseQueue() {
        return new Queue(orderResponseQueueName, true);
    }

    @Bean
    public TopicExchange orderResponseExchange() {
        return new TopicExchange(orderResponseExchangeName);
    }

    @Bean
    public Binding orderResponseBinding(
            Queue orderResponseQueue,
            TopicExchange orderResponseExchange
    ) {
        return BindingBuilder
                .bind(orderResponseQueue)
                .to(orderResponseExchange)
                .with(orderResponseRoutingKey);

    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
