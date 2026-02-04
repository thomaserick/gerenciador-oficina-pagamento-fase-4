package com.fiap.pj.core.pagamento.app.usecase;

import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;

public interface ProcessarPagamentoUseCase {

    void handle(ProcessarPagamentoCommand cmd);
}
