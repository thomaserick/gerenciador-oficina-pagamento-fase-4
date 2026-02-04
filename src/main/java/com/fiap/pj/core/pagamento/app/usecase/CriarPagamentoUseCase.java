package com.fiap.pj.core.pagamento.app.usecase;

import com.fiap.pj.core.pagamento.app.usecase.command.CriarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.Pagamento;

public interface CriarPagamentoUseCase {

    Pagamento handle(CriarPagamentoCommand cmd);
}
