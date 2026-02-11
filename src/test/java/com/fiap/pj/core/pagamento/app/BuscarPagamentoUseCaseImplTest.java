package com.fiap.pj.core.pagamento.app;

import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuscarPagamentoUseCaseImplTest {

    private PagamentoGateway pagamentoGateway;
    private BuscarPagamentoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        pagamentoGateway = mock(PagamentoGateway.class);
        useCase = new BuscarPagamentoUseCaseImpl(pagamentoGateway);
    }

    @Test
    void deveRetornarPagamentoQuandoEncontrado() {
        String ordemServicoId = "os-123";
        Pagamento pagamento = mock(Pagamento.class);

        when(pagamentoGateway.buscarPorOrdemServicoId(ordemServicoId))
                .thenReturn(Optional.of(pagamento));

        Pagamento resultado = useCase.handle(ordemServicoId);

        assertThat(resultado).isSameAs(pagamento);
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoNaoForEncontrado() {
        String ordemServicoId = "os-404";

        when(pagamentoGateway.buscarPorOrdemServicoId(ordemServicoId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.handle(ordemServicoId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pagamento não encontrado");
    }
}
