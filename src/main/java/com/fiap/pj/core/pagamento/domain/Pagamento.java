package com.fiap.pj.core.pagamento.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class Pagamento {

    private String pagamentoId;
    private String ordemServicoId;
    private String clienteId;
    private BigDecimal valor;
    private BigDecimal desconto;
    private BigDecimal valorTotal;
    private MetodoPagamento metodoPagamento;
    private Integer quantidadeParcelas;
    private StatusPagamento statusPagamento;
    private String transacaoId;
    private String codigoAutorizacao;
    private String codigoErro;
    private String mensagemErro;
    private String dataCriacao;
    private String dataAtualizacao;
    private String dataPagamento;
    private String dataExpiracao;
    private String chaveIdempotencia;
    private String criadoPor;

    /**
     * Dados do cartão para processamento do pagamento.
     * <p><b>IMPORTANTE:</b> Este campo NÃO deve ser persistido.</p>
     */
    private DadosCartao dadosCartao;

}
