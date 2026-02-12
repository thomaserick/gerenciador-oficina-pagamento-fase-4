package com.fiap.pj.core.pagamento.app;

import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ProcessarPagamentoUseCaseImplTest {

    private PagamentoGateway pagamentoGateway;
    private PagamentoPublisherGateway eventPublisher;
    private ProcessarPagamentoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        pagamentoGateway = mock(PagamentoGateway.class);
        eventPublisher = mock(PagamentoPublisherGateway.class);
        useCase = new ProcessarPagamentoUseCaseImpl(pagamentoGateway, eventPublisher);
    }

    @Test
    void deveProcessarPagamentoESalvarQuandoAutorizado() {
        ProcessarPagamentoCommand cmd = new ProcessarPagamentoCommand(
                "os-123",
                "cliente-1",
                BigDecimal.valueOf(100),
                BigDecimal.TEN,
                BigDecimal.valueOf(90),
                MetodoPagamento.CARTAO_CREDITO,
                3,
                "sistema-teste"
        );

        useCase.handle(cmd);

        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentoGateway, times(1)).save(pagamentoCaptor.capture());
        Pagamento pagamentoSalvo = pagamentoCaptor.getValue();

        assertThat(pagamentoSalvo.getOrdemServicoId()).isEqualTo(cmd.getOrdemServicoId());
        assertThat(pagamentoSalvo.getClienteId()).isEqualTo(cmd.getClienteId());
        assertThat(pagamentoSalvo.getValor()).isEqualTo(cmd.getValor());
        assertThat(pagamentoSalvo.getDesconto()).isEqualTo(cmd.getDesconto());
        assertThat(pagamentoSalvo.getValorTotal()).isEqualTo(cmd.getValorTotal());
        assertThat(pagamentoSalvo.getMetodoPagamento()).isEqualTo(cmd.getMetodoPagamento());
        assertThat(pagamentoSalvo.getQuantidadeParcelas()).isEqualTo(cmd.getQuantidadeParcelas());
        assertThat(pagamentoSalvo.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
        assertThat(pagamentoSalvo.getCriadoPor()).isEqualTo(cmd.getUsuarioId());
        assertThat(pagamentoSalvo.getDataCriacao()).isNotNull();
        assertThat(pagamentoSalvo.getPagamentoId()).isNotNull();

        verify(eventPublisher, times(1)).pagamentoRealizadoComSucesso(pagamentoSalvo);
    }

}
