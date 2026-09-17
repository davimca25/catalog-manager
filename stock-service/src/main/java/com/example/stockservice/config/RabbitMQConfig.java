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
    private String queueName;

    @Value("${rabbitmq.exchange.stock.name:stock.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.stock.key:stock.routing.key}")
    private String routingKey;

    @Bean
    public Queue stockQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding stockBinding(Queue stockQueue, TopicExchange stockExchange) {
        return BindingBuilder
                .bind(stockQueue)
                .to(stockExchange)
                .with(routingKey);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
