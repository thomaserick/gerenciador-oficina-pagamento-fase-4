package com.fiap.pj.infra.servico.persistense;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.requireNonNull;


@NoArgsConstructor
@Getter
public class PagamentoEntity {


    private UUID id;
    private String descricao;
    private BigDecimal valorUnitario;
    private String observacao;
    private boolean ativo;

    public PagamentoEntity(UUID id, String descricao, BigDecimal valorUnitario, String observacao, boolean ativo) {
        this.id = requireNonNull(id);
        this.descricao = requireNonNull(descricao);
        this.valorUnitario = valorUnitario;
        this.observacao = observacao;
        this.ativo = ativo;
    }

}
