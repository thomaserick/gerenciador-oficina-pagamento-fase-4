package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.fiap.pj.infra.pagamento.persistense.PagamentoEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PagamentoRepositoryMapper {

    public static PagamentoEntity toEntity(Pagamento pagamento) {
        if (pagamento == null) return null;
        PagamentoEntity entity = new PagamentoEntity();
        entity.setPagamentoId(pagamento.getPagamentoId());
        entity.setOrdemServicoId(pagamento.getOrdemServicoId());
        entity.setClienteId(pagamento.getClienteId());
        entity.setValor(pagamento.getValor());
        entity.setDesconto(pagamento.getDesconto());
        entity.setValorTotal(pagamento.getValorTotal());
        entity.setMetodoPagamento(
                pagamento.getMetodoPagamento().name()
        );
        entity.setQuantidadeParcelas(pagamento.getQuantidadeParcelas());
        entity.setStatusPagamento(
                pagamento.getStatusPagamento().name()
        );
        entity.setPagamentoExternoId(pagamento.getPagamentoExternoId());
        entity.setTransacaoId(pagamento.getTransacaoId());
        entity.setCodigoAutorizacao(pagamento.getCodigoAutorizacao());
        entity.setCodigoErro(pagamento.getCodigoErro());
        entity.setMensagemErro(pagamento.getMensagemErro());
        entity.setDataCriacao(pagamento.getDataCriacao());
        entity.setDataAtualizacao(pagamento.getDataAtualizacao());
        entity.setDataPagamento(pagamento.getDataPagamento());
        entity.setDataExpiracao(pagamento.getDataExpiracao());
        entity.setChaveIdempotencia(pagamento.getChaveIdempotencia());
        entity.setCriadoPor(pagamento.getCriadoPor());
        return entity;
    }

    public static Pagamento toDomain(PagamentoEntity entity) {
        if (entity == null) return null;
        Pagamento pagamento = new Pagamento();
        pagamento.setPagamentoId(entity.getPagamentoId());
        pagamento.setOrdemServicoId(entity.getOrdemServicoId());
        pagamento.setClienteId(entity.getClienteId());
        pagamento.setValor(entity.getValor());
        pagamento.setDesconto(entity.getDesconto());
        pagamento.setValorTotal(entity.getValorTotal());
        pagamento.setMetodoPagamento(
                MetodoPagamento.valueOf(entity.getMetodoPagamento())
        );
        pagamento.setQuantidadeParcelas(entity.getQuantidadeParcelas());
        pagamento.setStatusPagamento(
                StatusPagamento.valueOf(entity.getStatusPagamento())
        );
        pagamento.setPagamentoExternoId(entity.getPagamentoExternoId());
        pagamento.setTransacaoId(entity.getTransacaoId());
        pagamento.setCodigoAutorizacao(entity.getCodigoAutorizacao());
        pagamento.setCodigoErro(entity.getCodigoErro());
        pagamento.setMensagemErro(entity.getMensagemErro());
        pagamento.setDataCriacao(entity.getDataCriacao());
        pagamento.setDataAtualizacao(entity.getDataAtualizacao());
        pagamento.setDataPagamento(entity.getDataPagamento());
        pagamento.setDataExpiracao(entity.getDataExpiracao());
        pagamento.setChaveIdempotencia(entity.getChaveIdempotencia());
        pagamento.setCriadoPor(entity.getCriadoPor());
        return pagamento;
    }
}
