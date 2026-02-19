package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.event.PagamentoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class PagamentoPublisherGatewayImpl implements PagamentoPublisherGateway {

    final RabbitTemplate rabbitTemplate;

    @Value("${broker.queue.pagamento.autorizado}")
    private String routingKey;

    @Value("${broker.queue.pagamento.naoautorizado}")
    private String routingKeyNaoAutorizado;

    public PagamentoPublisherGatewayImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void pagamentoAutorizado(Pagamento pagamento) {
        var event = new PagamentoEvent(UUID.fromString(pagamento.getOrdemServicoId()));
        this.rabbitTemplate.convertAndSend(routingKey, event, message -> {
            message.getMessageProperties().setHeader("userId", pagamento.getCriadoPor());
            return message;
        });
    }

    @Override
    public void pagamentoNaoAturizado(Pagamento pagamento) {
        var event = new PagamentoEvent(UUID.fromString(pagamento.getOrdemServicoId()));
        this.rabbitTemplate.convertAndSend(routingKeyNaoAutorizado, event, message -> {
                message.getMessageProperties().setHeader("userId", pagamento.getCriadoPor());
            return message;
        });
    }
}