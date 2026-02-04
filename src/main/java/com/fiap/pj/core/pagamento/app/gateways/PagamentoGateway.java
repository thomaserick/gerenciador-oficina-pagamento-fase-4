package com.fiap.pj.core.pagamento.app.gateways;

import com.fiap.pj.core.pagamento.domain.Pagamento;

public interface PagamentoGateway {

    void save(Pagamento pagamento);

}
