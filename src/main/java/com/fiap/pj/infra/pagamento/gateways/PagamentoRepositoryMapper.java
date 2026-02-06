package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.fiap.pj.infra.pagamento.persistense.PagamentoEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PagamentoRepositoryMapper {

    public static PagamentoEntity toEntity(Pagamento pagamento) {
        return PagamentoEntity.builder()
                .pagamentoId(pagamento.getPagamentoId())
                .ordemServicoId(pagamento.getOrdemServicoId())
                .clienteId(pagamento.getClienteId())
                .valor(pagamento.getValor())
                .desconto(pagamento.getDesconto())
                .valorTotal(pagamento.getValorTotal())
                .metodoPagamento(pagamento.getMetodoPagamento().name())
                .quantidadeParcelas(pagamento.getQuantidadeParcelas())
                .statusPagamento(pagamento.getStatusPagamento().name())
                .transacaoId(pagamento.getTransacaoId())
                .codigoAutorizacao(pagamento.getCodigoAutorizacao())
                .codigoErro(pagamento.getCodigoErro())
                .mensagemErro(pagamento.getMensagemErro())
                .dataCriacao(pagamento.getDataCriacao())
                .dataAtualizacao(pagamento.getDataAtualizacao())
                .dataPagamento(pagamento.getDataPagamento())
                .dataExpiracao(pagamento.getDataExpiracao())
                .chaveIdempotencia(pagamento.getChaveIdempotencia())
                .criadoPor(pagamento.getCriadoPor())
                .build();
    }

    public static Pagamento toDomain(PagamentoEntity entity) {
        return Pagamento.builder()
                .pagamentoId(entity.getPagamentoId())
                .ordemServicoId(entity.getOrdemServicoId())
                .clienteId(entity.getClienteId())
                .valor(entity.getValor())
                .desconto(entity.getDesconto())
                .valorTotal(entity.getValorTotal())
                .metodoPagamento(MetodoPagamento.valueOf(entity.getMetodoPagamento()))
                .quantidadeParcelas(entity.getQuantidadeParcelas())
                .statusPagamento(StatusPagamento.valueOf(entity.getStatusPagamento()))
                .transacaoId(entity.getTransacaoId())
                .codigoAutorizacao(entity.getCodigoAutorizacao())
                .codigoErro(entity.getCodigoErro())
                .mensagemErro(entity.getMensagemErro())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .dataPagamento(entity.getDataPagamento())
                .dataExpiracao(entity.getDataExpiracao())
                .chaveIdempotencia(entity.getChaveIdempotencia())
                .criadoPor(entity.getCriadoPor())
                .build();
    }
}
