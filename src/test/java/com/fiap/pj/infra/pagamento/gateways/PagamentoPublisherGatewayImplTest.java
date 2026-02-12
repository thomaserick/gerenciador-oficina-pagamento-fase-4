package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.event.PagamentoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

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

        // injeta o valor da routingKey via reflexão (já que @Value não é processado no teste unitário puro)
        var routingKeyField = PagamentoPublisherGatewayImpl.class.getDeclaredField("routingKey");
        routingKeyField.setAccessible(true);
        routingKeyField.set(publisher, "fila-pagamento-processar");
    }

    @Test
    void devePublicarEventoDePagamentoComSucesso() throws Exception {
        Pagamento pagamento = mock(Pagamento.class);
        when(pagamento.getOrdemServicoId()).thenReturn("os-123");

        publisher.pagamentoAutorizado(pagamento);

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate, times(1))
                .convertAndSend(routingKeyCaptor.capture(), messageCaptor.capture());

        assertThat(routingKeyCaptor.getValue()).isEqualTo("fila-pagamento-processar");
        assertThat(messageCaptor.getValue()).isInstanceOf(PagamentoEvent.class);

        PagamentoEvent event = (PagamentoEvent) messageCaptor.getValue();
        assertThat(event.ordemServicoId()).isEqualTo("os-123");
    }
}
