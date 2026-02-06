package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.event.PagamentoRealizadoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class PagamentoPublisherGatewayImpl implements PagamentoPublisherGateway {

    final RabbitTemplate rabbitTemplate;

    @Value("${broker.queue.pagamento-processar}")
    private String routingKey;

    public PagamentoPublisherGatewayImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void pagamentoRealizadoComSucesso(Pagamento pagamento) {
        var message = new PagamentoRealizadoEvent(pagamento.getOrdemServicoId());
        rabbitTemplate.convertAndSend(routingKey, message);
    }
}
