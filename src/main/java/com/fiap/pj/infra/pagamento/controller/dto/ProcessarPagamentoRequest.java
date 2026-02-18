package com.fiap.pj.infra.pagamento.controller.dto;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para requisição de teste de pagamento.
 * Utilizado para simular o evento PagamentoProcessadoEvent via API REST.
 */
@Schema(description = "Request para testar processamento de pagamento")
public record ProcessarPagamentoRequest(

        @Schema(description = "ID da Ordem de Serviço", example = "OS-12345")
        @NotBlank(message = "ordemServicoId é obrigatório")
        String ordemServicoId,

        @Schema(description = "ID do Cliente", example = "CLI-001")
        @NotBlank(message = "clienteId é obrigatório")
        String clienteId,

        @Schema(description = "Valor bruto do pagamento", example = "150.00")
        @NotNull(message = "valor é obrigatório")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal valor,

        @Schema(description = "Valor do desconto", example = "10.00")
        BigDecimal desconto,

        @Schema(description = "Valor total a ser cobrado", example = "140.00")
        @NotNull(message = "valorTotal é obrigatório")
        @DecimalMin(value = "0.01", message = "valorTotal deve ser maior que zero")
        BigDecimal valorTotal,

        @Schema(description = "Método de pagamento", example = "CARTAO_CREDITO")
        @NotNull(message = "metodoPagamento é obrigatório")
        MetodoPagamento metodoPagamento,

        @Schema(description = "Quantidade de parcelas (1-12)", example = "3")
        @Min(value = 1, message = "quantidadeParcelas deve ser no mínimo 1")
        @Max(value = 12, message = "quantidadeParcelas deve ser no máximo 12")
        Integer quantidadeParcelas,

        @Schema(description = "ID do usuário que está realizando o pagamento", example = "user-teste")
        String usuarioId,

        @Schema(description = "Dados do cartão para processamento do pagamento")
        @Valid
        @NotNull(message = "dadosCartao é obrigatório")
        DadosCartaoRequest dadosCartao
) {

    /**
     * Retorna o desconto ou zero se não informado.
     */
    public BigDecimal getDescontoOuZero() {
        return this.desconto != null ? this.desconto : BigDecimal.ZERO;
    }

    /**
     * Retorna a quantidade de parcelas ou 1 se não informado.
     */
    public Integer getParcelasOuUma() {
        return this.quantidadeParcelas != null ? this.quantidadeParcelas : 1;
    }

    /**
     * Retorna o ID do usuário ou "sistema" se não informado.
     */
    public String getUsuarioOuSistema() {
        return this.usuarioId != null && !this.usuarioId.isBlank() ? this.usuarioId : "sistema";
    }
}

