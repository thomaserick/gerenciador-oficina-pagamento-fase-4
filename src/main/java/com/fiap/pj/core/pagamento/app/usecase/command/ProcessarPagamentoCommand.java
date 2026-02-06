package com.fiap.pj.core.pagamento.app.usecase.command;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProcessarPagamentoCommand {

    private final String ordemServicoId;
    private final String clienteId;
    private final BigDecimal valor;
    private final BigDecimal desconto;
    private final BigDecimal valorTotal;
    private final MetodoPagamento metodoPagamento;
    private final Integer quantidadeParcelas;
    private final String responsavel;

}


