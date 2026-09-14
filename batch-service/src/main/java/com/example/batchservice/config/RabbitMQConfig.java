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

    @Value("${rabbitmq.queue.order.name}")
    private String orderQueueName;

    @Value("${rabbitmq.exchange.stock.name}")
    private String stockExchangeName;

    @Value("${rabbitmq.queue.stock.name}")
    private String stockQueueName;

    @Value("${rabbitmq.routing.stock.key}")
    private String stockRoutingKey;

    @Bean
    public Queue orderQueue() {
        return new Queue(orderQueueName, true);
    }

    @Bean
    public Queue stockQueue() {
        return new Queue(stockQueueName, true);
    }

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(stockExchangeName);
    }

    @Bean
    public Binding stockBinding(Queue stockQueue, TopicExchange stockExchange) {
        return BindingBuilder
                .bind(stockQueue)
                .to(stockExchange)
                .with(stockRoutingKey);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
