package com.fiap.pj.infra.pagamento.controller.dto;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO de resposta do processamento de pagamento.
 */
@Schema(description = "Resposta do processamento de pagamento")
public record ProcessarPagamentoResponse(

        @Schema(description = "ID único do pagamento")
        String pagamentoId,

        @Schema(description = "ID da Ordem de Serviço")
        String ordemServicoId,

        @Schema(description = "ID da transação no Mercado Pago")
        String transacaoId,

        @Schema(description = "Status do pagamento")
        StatusPagamento status,

        @Schema(description = "Código de autorização (quando aprovado)")
        String codigoAutorizacao,

        @Schema(description = "Valor total cobrado")
        BigDecimal valorTotal,

        @Schema(description = "Método de pagamento utilizado")
        MetodoPagamento metodoPagamento,

        @Schema(description = "Quantidade de parcelas")
        Integer quantidadeParcelas,

        @Schema(description = "Código de erro (quando rejeitado)")
        String codigoErro,

        @Schema(description = "Mensagem de erro (quando rejeitado)")
        String mensagemErro,

        @Schema(description = "Mensagem descritiva do resultado")
        String mensagem
) {

    /**
     * Cria uma resposta a partir de um objeto Pagamento.
     */
    public static ProcessarPagamentoResponse fromPagamento(Pagamento pagamento) {
        String mensagem = switch (pagamento.getStatusPagamento()) {
            case AUTORIZADO -> "Pagamento aprovado com sucesso!";
            case PENDENTE -> "Pagamento pendente de confirmação.";
            case NAO_AUTORIZADO -> "Pagamento não autorizado: " +
                    (pagamento.getMensagemErro() != null ? pagamento.getMensagemErro() : "Erro desconhecido");
            case CANCELADO -> "Pagamento cancelado.";
            case ESTORNADO -> "Pagamento estornado.";
        };

        return new ProcessarPagamentoResponse(
                pagamento.getPagamentoId(),
                pagamento.getOrdemServicoId(),
                pagamento.getTransacaoId(),
                pagamento.getStatusPagamento(),
                pagamento.getCodigoAutorizacao(),
                pagamento.getValorTotal(),
                pagamento.getMetodoPagamento(),
                pagamento.getQuantidadeParcelas(),
                pagamento.getCodigoErro(),
                pagamento.getMensagemErro(),
                mensagem
        );
    }
}

