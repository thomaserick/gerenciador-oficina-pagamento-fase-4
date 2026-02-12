package com.fiap.pj.core.pagamento.domain.event;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;

import java.math.BigDecimal;

public record PagamentoProcessadoEvent(String ordemServicoId,
                                       String clienteId,
                                       BigDecimal valor,
                                       BigDecimal desconto,
                                       BigDecimal valorTotal,
                                       MetodoPagamento metodoPagamento,
                                       Integer quantidadeParcelas,
                                       String usuarioId) {
}
