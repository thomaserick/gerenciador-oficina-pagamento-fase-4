package com.fiap.pj.core.pagamento.app.usecase;

import com.fiap.pj.infra.pagamento.controller.request.ListarPagamentoRequest;
import com.fiap.pj.infra.pagamento.controller.response.PagamentoResponse;

public interface ListarPagamentoUseCase {

    PagamentoResponse handle(ListarPagamentoRequest request);
}
