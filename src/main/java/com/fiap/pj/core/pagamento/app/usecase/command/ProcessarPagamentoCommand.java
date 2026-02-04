package com.fiap.pj.core.pagamento.app.usecase.command;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProcessarPagamentoCommand {

    private final String ordemServicoId;
    private final String clienteId;
    private final BigDecimal valor;
    private final BigDecimal desconto;
    private final BigDecimal valorTotal;
    private final MetodoPagamento metodoPagamento;
    private final Integer quantidadeParcelas;

    public ProcessarPagamentoCommand(String ordemServicoId, String clienteId, BigDecimal valor, BigDecimal desconto, BigDecimal valorTotal, MetodoPagamento metodoPagamento, Integer quantidadeParcelas) {
        this.ordemServicoId = ordemServicoId;
        this.clienteId = clienteId;
        this.valor = valor;
        this.desconto = desconto;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.quantidadeParcelas = quantidadeParcelas;
    }
}


