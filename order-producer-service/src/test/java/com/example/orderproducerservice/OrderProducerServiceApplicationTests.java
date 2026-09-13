package com.example.orderproducerservice;

import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class OrderProducerServiceApplicationTests {

    @MockitoBean
    private ConnectionFactory connectionFactory;

    @Test
    void contextLoads() {
    }

}
