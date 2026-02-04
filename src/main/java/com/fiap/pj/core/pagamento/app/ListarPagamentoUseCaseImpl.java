package com.fiap.pj.core.pagamento.app;


import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.usecase.ListarPagamentoUseCase;
import com.fiap.pj.infra.pagamento.controller.request.ListarPagamentoRequest;
import com.fiap.pj.infra.pagamento.controller.response.PagamentoResponse;


public class ListarPagamentoUseCaseImpl implements ListarPagamentoUseCase {

    private final PagamentoGateway pagamentoGateway;

    public ListarPagamentoUseCaseImpl(PagamentoGateway pagamentoGateway) {
        this.pagamentoGateway = pagamentoGateway;
    }

    @Override
    public PagamentoResponse handle(ListarPagamentoRequest request) {
        return null;
    }
}
