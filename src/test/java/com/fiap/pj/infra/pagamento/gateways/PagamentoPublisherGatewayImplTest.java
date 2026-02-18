package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.event.PagamentoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PagamentoPublisherGatewayImplTest {

    private RabbitTemplate rabbitTemplate;
    private PagamentoPublisherGatewayImpl publisher;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new PagamentoPublisherGatewayImpl(rabbitTemplate);

        var routingKeyField = PagamentoPublisherGatewayImpl.class.getDeclaredField("routingKey");
        routingKeyField.setAccessible(true);
        routingKeyField.set(publisher, "fila-pagamento-processar");

        var routingKeyNaoAutorizadoField =
                PagamentoPublisherGatewayImpl.class.getDeclaredField("routingKeyNaoAutorizado");
        routingKeyNaoAutorizadoField.setAccessible(true);
        routingKeyNaoAutorizadoField.set(publisher, "fila-pagamento-nao-autorizado");
    }

    @Test
    void devePublicarEventoDePagamentoComSucesso() {
        UUID ordemServicoId = UUID.randomUUID();
        Pagamento pagamento = mock(Pagamento.class);
        when(pagamento.getOrdemServicoId()).thenReturn(ordemServicoId.toString());
        when(pagamento.getCriadoPor()).thenReturn("user-1");

        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        publisher.pagamentoAutorizado(pagamento);

        verify(rabbitTemplate, times(1))
                .convertAndSend(routingKeyCaptor.capture(), messageCaptor.capture(), postProcessorCaptor.capture());

        assertThat(routingKeyCaptor.getValue()).isEqualTo("fila-pagamento-processar");
        assertThat(messageCaptor.getValue()).isInstanceOf(PagamentoEvent.class);

        PagamentoEvent event = (PagamentoEvent) messageCaptor.getValue();
        assertThat(event.ordemServicoId()).isEqualTo(ordemServicoId);
    }

    @Test
    void devePublicarEventoDePagamentoNaoAutorizadoComSucesso() {
        UUID ordemServicoId = UUID.randomUUID();
        Pagamento pagamento = mock(Pagamento.class);
        when(pagamento.getOrdemServicoId()).thenReturn(ordemServicoId.toString());
        when(pagamento.getCriadoPor()).thenReturn("user-erro");

        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        publisher.pagamentoNaoAturizado(pagamento);

        verify(rabbitTemplate, times(1))
                .convertAndSend(routingKeyCaptor.capture(), messageCaptor.capture(), postProcessorCaptor.capture());

        assertThat(routingKeyCaptor.getValue()).isEqualTo("fila-pagamento-nao-autorizado");
        assertThat(messageCaptor.getValue()).isInstanceOf(PagamentoEvent.class);

        PagamentoEvent event = (PagamentoEvent) messageCaptor.getValue();
        assertThat(event.ordemServicoId()).isEqualTo(ordemServicoId);
    }
}
