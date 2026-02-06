package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.fiap.pj.infra.pagamento.persistense.PagamentoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PagamentoRepositoryMapperTest {

    @Test
    @DisplayName("Deve mapear Pagamento para PagamentoEntity corretamente")
    void deveMapearDomainParaEntity() {
        Pagamento pagamento = Pagamento.builder()
                .pagamentoId("pg-123")
                .ordemServicoId("os-456")
                .clienteId("cliente-789")
                .valor(BigDecimal.valueOf(100.00))
                .desconto(BigDecimal.TEN)
                .valorTotal(BigDecimal.valueOf(90.00))
                .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                .quantidadeParcelas(3)
                .statusPagamento(StatusPagamento.AUTORIZADO)
                .transacaoId("txn-001")
                .codigoAutorizacao("auth-999")
                .codigoErro("0")
                .mensagemErro("ok")
                .dataCriacao("2025-02-01T10:00:00")
                .dataAtualizacao("2025-02-01T10:05:00")
                .dataPagamento("2025-02-01")
                .dataExpiracao("2025-03-01")
                .chaveIdempotencia("idem-123")
                .criadoPor("sistema-teste")
                .build();

        PagamentoEntity entity = PagamentoRepositoryMapper.toEntity(pagamento);

        assertThat(entity.getPagamentoId()).isEqualTo("pg-123");
        assertThat(entity.getOrdemServicoId()).isEqualTo("os-456");
        assertThat(entity.getClienteId()).isEqualTo("cliente-789");
        assertThat(entity.getValor()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(entity.getDesconto()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(entity.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(entity.getMetodoPagamento()).isEqualTo(MetodoPagamento.CARTAO_CREDITO.name());
        assertThat(entity.getQuantidadeParcelas()).isEqualTo(3);
        assertThat(entity.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO.name());
        assertThat(entity.getTransacaoId()).isEqualTo("txn-001");
        assertThat(entity.getCodigoAutorizacao()).isEqualTo("auth-999");
        assertThat(entity.getCodigoErro()).isEqualTo("0");
        assertThat(entity.getMensagemErro()).isEqualTo("ok");
        assertThat(entity.getDataCriacao()).isEqualTo("2025-02-01T10:00:00");
        assertThat(entity.getDataAtualizacao()).isEqualTo("2025-02-01T10:05:00");
        assertThat(entity.getDataPagamento()).isEqualTo("2025-02-01");
        assertThat(entity.getDataExpiracao()).isEqualTo("2025-03-01");
        assertThat(entity.getChaveIdempotencia()).isEqualTo("idem-123");
        assertThat(entity.getCriadoPor()).isEqualTo("sistema-teste");
    }

    @Test
    @DisplayName("Deve mapear PagamentoEntity para Pagamento corretamente")
    void deveMapearEntityParaDomain() {
        PagamentoEntity entity = PagamentoEntity.builder()
                .pagamentoId("pg-123")
                .ordemServicoId("os-456")
                .clienteId("cliente-789")
                .valor(BigDecimal.valueOf(100.00))
                .desconto(BigDecimal.TEN)
                .valorTotal(BigDecimal.valueOf(90.00))
                .metodoPagamento(MetodoPagamento.CARTAO_CREDITO.name())
                .quantidadeParcelas(3)
                .statusPagamento(StatusPagamento.AUTORIZADO.name())
                .transacaoId("txn-001")
                .codigoAutorizacao("auth-999")
                .codigoErro("0")
                .mensagemErro("ok")
                .dataCriacao("2025-02-01T10:00:00")
                .dataAtualizacao("2025-02-01T10:05:00")
                .dataPagamento("2025-02-01")
                .dataExpiracao("2025-03-01")
                .chaveIdempotencia("idem-123")
                .criadoPor("sistema-teste")
                .build();

        Pagamento pagamento = PagamentoRepositoryMapper.toDomain(entity);

        assertThat(pagamento.getPagamentoId()).isEqualTo("pg-123");
        assertThat(pagamento.getOrdemServicoId()).isEqualTo("os-456");
        assertThat(pagamento.getClienteId()).isEqualTo("cliente-789");
        assertThat(pagamento.getValor()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(pagamento.getDesconto()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(pagamento.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(pagamento.getMetodoPagamento()).isEqualTo(MetodoPagamento.CARTAO_CREDITO);
        assertThat(pagamento.getQuantidadeParcelas()).isEqualTo(3);
        assertThat(pagamento.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
        assertThat(pagamento.getTransacaoId()).isEqualTo("txn-001");
        assertThat(pagamento.getCodigoAutorizacao()).isEqualTo("auth-999");
        assertThat(pagamento.getCodigoErro()).isEqualTo("0");
        assertThat(pagamento.getMensagemErro()).isEqualTo("ok");
        assertThat(pagamento.getDataCriacao()).isEqualTo("2025-02-01T10:00:00");
        assertThat(pagamento.getDataAtualizacao()).isEqualTo("2025-02-01T10:05:00");
        assertThat(pagamento.getDataPagamento()).isEqualTo("2025-02-01");
        assertThat(pagamento.getDataExpiracao()).isEqualTo("2025-03-01");
        assertThat(pagamento.getChaveIdempotencia()).isEqualTo("idem-123");
        assertThat(pagamento.getCriadoPor()).isEqualTo("sistema-teste");
    }
}
