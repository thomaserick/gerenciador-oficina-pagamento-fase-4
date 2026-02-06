package com.fiap.pj.infra.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Value("${broker.queue.pagamento.autorizado}")
    private String queuePagamento;

    @Bean
    public Queue queuePagamento() {
        return new Queue(queuePagamento, true);
    }

    @Value("${broker.queue.pagamento.processar}")
    private String queueProcessarPagamento;

    @Bean
    public Queue queueProcessarPagamento() {
        return new Queue(queueProcessarPagamento, true);
    }

    @Bean
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }


}
