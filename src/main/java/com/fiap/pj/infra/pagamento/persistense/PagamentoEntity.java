package com.fiap.pj.infra.pagamento.persistense;


import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.math.BigDecimal;


@DynamoDbBean
@Setter
public class PagamentoEntity {

    private String pagamentoId;
    private String ordemServicoId;
    private String clienteId;
    private BigDecimal valor;
    private BigDecimal desconto;
    private BigDecimal valorTotal;
    private String metodoPagamento;
    private Integer quantidadeParcelas;
    private String statusPagamento;

    // Integração externa
    private String pagamentoExternoId;
    private String transacaoId;
    private String codigoAutorizacao;

    // Erros
    private String codigoErro;
    private String mensagemErro;


    private String dataCriacao;
    private String dataAtualizacao;
    private String dataPagamento;
    private String dataExpiracao;

    // Auditoria
    private String chaveIdempotencia;
    private String criadoPor;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("pagamento_id")
    public String getPagamentoId() {
        return pagamentoId;
    }

    @DynamoDbAttribute("ordem_servico_id")
    @DynamoDbSecondaryPartitionKey(indexNames = "ordemServico-index")
    public String getOrdemServicoId() {
        return ordemServicoId;
    }

    @DynamoDbAttribute("cliente_id")
    public String getClienteId() {
        return clienteId;
    }

    @DynamoDbAttribute("valor")
    public BigDecimal getValor() {
        return valor;
    }

    @DynamoDbAttribute("desconto")
    public BigDecimal getDesconto() {
        return desconto;
    }

    @DynamoDbAttribute("valor_total")
    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    @DynamoDbAttribute("metodo_pagamento")
    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    @DynamoDbAttribute("quantidade_parcelas")
    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    @DynamoDbAttribute("status_pagamento")
    public String getStatusPagamento() {
        return statusPagamento;
    }

    @DynamoDbAttribute("pagamento_externo_id")
    public String getPagamentoExternoId() {
        return pagamentoExternoId;
    }

    @DynamoDbAttribute("transacao_id")
    public String getTransacaoId() {
        return transacaoId;
    }

    @DynamoDbAttribute("codigo_autorizacao")
    public String getCodigoAutorizacao() {
        return codigoAutorizacao;
    }

    @DynamoDbAttribute("codigo_erro")
    public String getCodigoErro() {
        return codigoErro;
    }

    @DynamoDbAttribute("mensagem_erro")
    public String getMensagemErro() {
        return mensagemErro;
    }

    @DynamoDbAttribute("data_criacao")
    public String getDataCriacao() {
        return dataCriacao;
    }

    @DynamoDbAttribute("data_atualizacao")
    public String getDataAtualizacao() {
        return dataAtualizacao;
    }

    @DynamoDbAttribute("data_pagamento")
    public String getDataPagamento() {
        return dataPagamento;
    }

    @DynamoDbAttribute("data_expiracao")
    public String getDataExpiracao() {
        return dataExpiracao;
    }

    @DynamoDbAttribute("chave_idempotencia")
    public String getChaveIdempotencia() {
        return chaveIdempotencia;
    }

    @DynamoDbAttribute("criado_por")
    public String getCriadoPor() {
        return criadoPor;
    }
}
