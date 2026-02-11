package com.fiap.pj.core.pagamento.app.gateways;

import com.fiap.pj.core.pagamento.domain.Pagamento;

import java.util.Optional;

public interface PagamentoGateway {

    void save(Pagamento pagamento);

    Optional<Pagamento> buscarPorOrdemServicoId(String ordemServicoId);

}
