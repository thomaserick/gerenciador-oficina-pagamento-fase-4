package com.fiap.pj.core.pagamento.app;


import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.usecase.BuscarPagamentoUseCase;
import com.fiap.pj.core.pagamento.domain.Pagamento;


public class BuscarPagamentoUseCaseImpl implements BuscarPagamentoUseCase {

    private final PagamentoGateway gateway;

    public BuscarPagamentoUseCaseImpl(PagamentoGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Pagamento handle(String ordemServicoId) {
        return gateway.buscarPorOrdemServicoId(ordemServicoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }
}
