package com.fiap.pj.core.pagamento.app.usecase;

import com.fiap.pj.infra.servico.controller.request.ListarPagamentoRequest;
import com.fiap.pj.infra.servico.controller.response.PagamentoResponse;

public interface ListarPagamentoUseCase {

    PagamentoResponse handle(ListarPagamentoRequest request);
}
