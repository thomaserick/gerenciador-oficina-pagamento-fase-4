package com.fiap.pj.core.pagamento.domain.event;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;

import java.math.BigDecimal;

/**
 * Evento de pagamento a ser processado.
 *
 * <p>Inclui os dados do cartão necessários para processamento do pagamento
 * via gateway do Mercado Pago.</p>
 */
public record PagamentoProcessadoEvent(
        String ordemServicoId,
        String clienteId,
        BigDecimal valor,
        BigDecimal desconto,
        BigDecimal valorTotal,
        MetodoPagamento metodoPagamento,
        Integer quantidadeParcelas,
        String usuarioId,
        // Dados do cartão
        String numeroCartao,
        String codigoSeguranca,
        Integer mesExpiracao,
        Integer anoExpiracao,
        String nomeTitular,
        String cpfTitular,
        String emailTitular
) {

    /**
     * Construtor de compatibilidade sem dados de cartão (para testes legados).
     */
    public PagamentoProcessadoEvent(
            String ordemServicoId,
            String clienteId,
            BigDecimal valor,
            BigDecimal desconto,
            BigDecimal valorTotal,
            MetodoPagamento metodoPagamento,
            Integer quantidadeParcelas,
            String usuarioId
    ) {
        this(ordemServicoId, clienteId, valor, desconto, valorTotal, metodoPagamento,
             quantidadeParcelas, usuarioId, null, null, null, null, null, null, null);
    }

    /**
     * Verifica se os dados do cartão foram preenchidos.
     */
    public boolean possuiDadosCartao() {
        return this.numeroCartao != null && !this.numeroCartao.isBlank()
                && this.codigoSeguranca != null && !this.codigoSeguranca.isBlank()
                && this.mesExpiracao != null
                && this.anoExpiracao != null
                && this.nomeTitular != null && !this.nomeTitular.isBlank()
                && this.cpfTitular != null && !this.cpfTitular.isBlank();
    }
}
