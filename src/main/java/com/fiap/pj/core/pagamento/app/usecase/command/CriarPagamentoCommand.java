package com.fiap.pj.core.pagamento.app.usecase.command;

import lombok.Getter;

@Getter
public class CriarPagamentoCommand {

    public final String ordemServicoId;

    public CriarPagamentoCommand(String ordemServicoId) {
        this.ordemServicoId = ordemServicoId;
    }
}


