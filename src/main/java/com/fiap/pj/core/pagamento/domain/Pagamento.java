package com.fiap.pj.core.pagamento.domain;


import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Getter
public class Pagamento {


    private UUID id;
    private String descricao;
    private BigDecimal valorUnitario;
    private String observacao;
    private boolean ativo;

    public Pagamento(UUID id, String descricao, BigDecimal valorUnitario, String observacao, boolean ativo) {
        this.id = requireNonNull(id);
        this.descricao = requireNonNull(descricao);
        this.valorUnitario = valorUnitario;
        this.observacao = observacao;
        this.ativo = ativo;
    }

}
