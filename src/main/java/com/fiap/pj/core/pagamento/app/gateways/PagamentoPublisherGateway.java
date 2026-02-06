package com.fiap.pj.core.pagamento.app.gateways;

import com.fiap.pj.core.pagamento.domain.Pagamento;

public interface PagamentoPublisherGateway {

    void pagamentoRealizadoComSucesso(Pagamento pagamento);
}
