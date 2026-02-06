package com.fiap.pj.infra.pagamento.consumer;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;

import java.math.BigDecimal;

public record ProcessarPagamentoMessage(String ordemServicoId,
                                        String clienteId,
                                        BigDecimal valor,
                                        BigDecimal desconto,
                                        BigDecimal valorTotal,
                                        MetodoPagamento metodoPagamento,
                                        Integer quantidadeParcelas,
                                        String responsavel) {
}
