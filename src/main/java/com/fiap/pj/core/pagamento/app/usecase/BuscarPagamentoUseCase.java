package com.fiap.pj.core.pagamento.app.usecase;

import com.fiap.pj.core.pagamento.domain.Pagamento;

public interface BuscarPagamentoUseCase {

    Pagamento handle(String ordemServicoId);
}
