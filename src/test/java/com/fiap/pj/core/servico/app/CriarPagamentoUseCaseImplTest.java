package com.fiap.pj.core.servico.app;


import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CriarPagamentoUseCaseImplTest {

    @Mock
    private PagamentoGateway pagamentoGateway;


    @Test
    void deveCriarServico() {
//        when(pagamentoGateway.salvar(any(Pagamento.class))).thenReturn(PagamentoTestFactory.umServico());
//
//        var service = criarServicoUseCaseImpl.handle(PagamentoTestFactory.umCriarServicoCommand());
//
//        assertNotNull(service);
//        assertEquals(PagamentoTestFactory.ID, service.getId());
//        assertEquals(PagamentoTestFactory.DESCRICAO, service.getDescricao());
//        assertEquals(PagamentoTestFactory.VALOR_UNITARIO, service.getValorUnitario());
//        assertEquals(PagamentoTestFactory.OBSERVACAO, service.getObservacao());

    }
}
