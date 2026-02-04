package com.fiap.pj.infra.servico.gateways;

import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.infra.servico.persistense.PagamentoEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PagamentoRepositoryMapper {

    public static PagamentoEntity mapToTable(Pagamento pagamento) {
        return new PagamentoEntity(
                pagamento.getId(),
                pagamento.getDescricao(),
                pagamento.getValorUnitario(),
                pagamento.getObservacao(),
                pagamento.isAtivo()
        );
    }

    public static Pagamento mapToDomain(PagamentoEntity entity) {
        return new Pagamento(
                entity.getId(),
                entity.getDescricao(),
                entity.getValorUnitario(),
                entity.getObservacao(),
                entity.isAtivo()
        );
    }
}
