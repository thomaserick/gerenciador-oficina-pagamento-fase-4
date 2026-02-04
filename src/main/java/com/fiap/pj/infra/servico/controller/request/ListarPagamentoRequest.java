package com.fiap.pj.infra.servico.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ListarPagamentoRequest {


    private String nome;

    private Boolean ativo;


}
