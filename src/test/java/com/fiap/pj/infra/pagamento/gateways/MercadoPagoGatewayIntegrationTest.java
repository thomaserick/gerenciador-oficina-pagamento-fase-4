package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para validar a comunicação real com a API do Mercado Pago.
 *
 * <p>Estes testes requerem credenciais válidas do Mercado Pago configuradas via
 * variáveis de ambiente:</p>
 * <ul>
 *   <li>MERCADO_PAGO_ACCESS_TOKEN - Token de acesso sandbox</li>
 *   <li>MERCADO_PAGO_PUBLIC_KEY - Chave pública sandbox</li>
 * </ul>
 *
 * <p>Para executar os testes de integração, use:</p>
 * <pre>mvn test -Dgroups=integration</pre>
 */
@Tag("integration")
@DisplayName("MercadoPagoGatewayImpl - Testes de Integração")
class MercadoPagoGatewayIntegrationTest {

    private MercadoPagoGatewayImpl gateway;

    // Credenciais de teste do Mercado Pago (sandbox)
    private static final String TEST_ACCESS_TOKEN = System.getenv("MERCADO_PAGO_ACCESS_TOKEN") != null
            ? System.getenv("MERCADO_PAGO_ACCESS_TOKEN")
            : "TEST-4604387371249325-021610-a9360cf42beb4662ed601e25048d2adb-376051986";

    private static final String TEST_PUBLIC_KEY = System.getenv("MERCADO_PAGO_PUBLIC_KEY") != null
            ? System.getenv("MERCADO_PAGO_PUBLIC_KEY")
            : "TEST-b119b7dd-037a-43a9-afe3-bfdfe8d2ef3f";

    @BeforeEach
    void setUp() {
        this.gateway = new MercadoPagoGatewayImpl();

        // Configura as credenciais
        ReflectionTestUtils.setField(this.gateway, "accessToken", TEST_ACCESS_TOKEN);
        ReflectionTestUtils.setField(this.gateway, "publicKey", TEST_PUBLIC_KEY);

        // Inicializa o gateway
        this.gateway.init();
    }

    @Test
    @DisplayName("Deve processar pagamento com cartão de teste e retornar APROVADO")
    void deveProcessarPagamentoComCartaoTesteERetornarAprovado() {
        // Arrange
        Pagamento pagamento = this.criarPagamentoTeste();

        // Act
        Pagamento resultado = this.gateway.processarPagamento(pagamento);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
        assertThat(resultado.getTransacaoId()).isNotNull();
        assertThat(resultado.getTransacaoId()).isNotEmpty();
        assertThat(resultado.getChaveIdempotencia()).isNotNull();

        System.out.println("✅ Pagamento processado com sucesso!");
        System.out.println("   - Transação ID: " + resultado.getTransacaoId());
        System.out.println("   - Status: " + resultado.getStatusPagamento());
        System.out.println("   - Código Autorização: " + resultado.getCodigoAutorizacao());
    }

    @Test
    @DisplayName("Deve gerar chave de idempotência automaticamente")
    void deveGerarChaveIdempotenciaAutomaticamente() {
        // Arrange
        Pagamento pagamento = this.criarPagamentoTeste();
        pagamento.setChaveIdempotencia(null);

        // Act
        Pagamento resultado = this.gateway.processarPagamento(pagamento);

        // Assert
        assertThat(resultado.getChaveIdempotencia()).isNotNull();
        assertThat(resultado.getChaveIdempotencia()).isNotEmpty();
    }

    @Test
    @DisplayName("Deve processar pagamento com parcelas")
    void deveProcessarPagamentoComParcelas() {
        // Arrange
        Pagamento pagamento = this.criarPagamentoTeste();
        pagamento.setQuantidadeParcelas(3);

        // Act
        Pagamento resultado = this.gateway.processarPagamento(pagamento);

        // Assert
        assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);

        System.out.println("✅ Pagamento parcelado processado!");
        System.out.println("   - Parcelas: 3x");
        System.out.println("   - Status: " + resultado.getStatusPagamento());
    }

    @Test
    @DisplayName("Deve manter chave de idempotência após processamento")
    void deveManterChaveIdempotenciaAposProcessamento() {
        // Arrange
        String chaveIdempotencia = UUID.randomUUID().toString();

        Pagamento pagamento = this.criarPagamentoTeste();
        pagamento.setChaveIdempotencia(chaveIdempotencia);

        // Act
        Pagamento resultado = this.gateway.processarPagamento(pagamento);

        // Assert
        // A chave de idempotência deve ser mantida após o processamento
        assertThat(resultado.getChaveIdempotencia()).isEqualTo(chaveIdempotencia);
        assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);

        System.out.println("✅ Chave de idempotência mantida!");
        System.out.println("   - Chave: " + chaveIdempotencia);
        System.out.println("   - Transação: " + resultado.getTransacaoId());
    }

    @Test
    @DisplayName("Deve processar múltiplos pagamentos diferentes")
    void deveProcessarMultiplosPagamentosDiferentes() {
        // Arrange
        Pagamento pagamento1 = this.criarPagamentoTeste();
        pagamento1.setValorTotal(BigDecimal.valueOf(50.00));

        Pagamento pagamento2 = this.criarPagamentoTeste();
        pagamento2.setValorTotal(BigDecimal.valueOf(75.00));

        // Act
        Pagamento resultado1 = this.gateway.processarPagamento(pagamento1);
        Pagamento resultado2 = this.gateway.processarPagamento(pagamento2);

        // Assert
        assertThat(resultado1.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
        assertThat(resultado2.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
        assertThat(resultado1.getTransacaoId()).isNotEqualTo(resultado2.getTransacaoId());

        System.out.println("✅ Múltiplos pagamentos processados!");
        System.out.println("   - Pagamento 1: R$ 50.00 - " + resultado1.getTransacaoId());
        System.out.println("   - Pagamento 2: R$ 75.00 - " + resultado2.getTransacaoId());
    }

    private Pagamento criarPagamentoTeste() {
        return Pagamento.builder()
                .pagamentoId(UUID.randomUUID().toString())
                .ordemServicoId("os-" + UUID.randomUUID().toString().substring(0, 8))
                .clienteId("cliente-teste")
                .valor(BigDecimal.valueOf(100.00))
                .desconto(BigDecimal.ZERO)
                .valorTotal(BigDecimal.valueOf(100.00))
                .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                .quantidadeParcelas(1)
                .statusPagamento(StatusPagamento.PENDENTE)
                .build();
    }
}

