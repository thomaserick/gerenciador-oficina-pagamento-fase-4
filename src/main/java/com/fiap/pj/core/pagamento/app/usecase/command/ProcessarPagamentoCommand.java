package com.fiap.pj.core.pagamento.app.usecase.command;

import com.fiap.pj.core.pagamento.domain.DadosCartao;
import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.infra.pagamento.controller.dto.DadosCartaoRequest;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProcessarPagamentoCommand {

    private final String ordemServicoId;
    private final String clienteId;
    private final BigDecimal valor;
    private final BigDecimal desconto;
    private final BigDecimal valorTotal;
    private final MetodoPagamento metodoPagamento;
    private final Integer quantidadeParcelas;
    private final String usuarioId;
    private final DadosCartao dadosCartao;

    /**
     * Construtor completo com dados do cartão.
     */
    public ProcessarPagamentoCommand(
            String ordemServicoId,
            String clienteId,
            BigDecimal valor,
            BigDecimal desconto,
            BigDecimal valorTotal,
            MetodoPagamento metodoPagamento,
            Integer quantidadeParcelas,
            String usuarioId,
            DadosCartao dadosCartao
    ) {
        this.ordemServicoId = ordemServicoId;
        this.clienteId = clienteId;
        this.valor = valor;
        this.desconto = desconto;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.quantidadeParcelas = quantidadeParcelas;
        this.usuarioId = usuarioId;
        this.dadosCartao = dadosCartao;
    }

    /**
     * Construtor de compatibilidade sem dados de cartão (para testes legados).
     */
    public ProcessarPagamentoCommand(
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
             quantidadeParcelas, usuarioId, null);
    }

    /**
     * Mapeia os dados do cartão do DTO para o domínio.
     */
    public static DadosCartao mapearDadosCartao(DadosCartaoRequest dto) {
        return DadosCartao.builder()
                .numeroCartao(dto.numeroCartao())
                .codigoSeguranca(dto.codigoSeguranca())
                .mesExpiracao(dto.mesExpiracao())
                .anoExpiracao(dto.anoExpiracao())
                .nomeTitular(dto.nomeTitular())
                .cpfTitular(dto.cpfTitular())
                .emailTitular(dto.getEmailOuPadrao())
                .build();
    }
}


