package com.fiap.pj.infra.pagamento.consumer;

import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.event.PagamentoProcessadoEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PagamentoConsumerTest {

    @Test
    void deveChamarUseCaseComCommandCorretoAoReceberMensagem() {
        ProcessarPagamentoUseCase useCase = mock(ProcessarPagamentoUseCase.class);
        PagamentoConsumer consumer = new PagamentoConsumer(useCase);

        PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                "os-123",
                "cli-456",
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("90.00"),
                MetodoPagamento.CARTAO_CREDITO,
                3,
                "user-1"
        );

        consumer.receiveMessage(event);

        ArgumentCaptor<ProcessarPagamentoCommand> cmdCaptor =
                ArgumentCaptor.forClass(ProcessarPagamentoCommand.class);

        verify(useCase, times(1)).handle(cmdCaptor.capture());

        ProcessarPagamentoCommand cmd = cmdCaptor.getValue();

        Assertions.assertEquals("os-123", cmd.getOrdemServicoId());
        Assertions.assertEquals(new BigDecimal("100.00"), cmd.getValor());
        Assertions.assertEquals(new BigDecimal("10.00"), cmd.getDesconto());
        Assertions.assertEquals(new BigDecimal("90.00"), cmd.getValorTotal());
        Assertions.assertEquals(MetodoPagamento.CARTAO_CREDITO, cmd.getMetodoPagamento());
        Assertions.assertEquals(3, cmd.getQuantidadeParcelas());
        Assertions.assertEquals("user-1", cmd.getUsuarioId());

    }
}
