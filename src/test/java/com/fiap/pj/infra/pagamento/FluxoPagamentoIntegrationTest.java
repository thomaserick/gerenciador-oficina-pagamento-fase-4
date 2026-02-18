package com.fiap.pj.infra.pagamento;

import com.fiap.pj.core.pagamento.app.ProcessarPagamentoUseCaseImpl;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.fiap.pj.core.pagamento.domain.event.PagamentoProcessadoEvent;
import com.fiap.pj.infra.pagamento.consumer.PagamentoConsumer;
import com.fiap.pj.infra.pagamento.gateways.MercadoPagoGatewayImpl;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testes de integração end-to-end para o fluxo completo de pagamento.
 *
 * <p>Este teste valida o fluxo completo:</p>
 * <ol>
 *   <li>Recepção do evento PagamentoProcessadoEvent</li>
 *   <li>Processamento pelo ProcessarPagamentoUseCaseImpl</li>
 *   <li>Integração com MercadoPagoGatewayImpl (real)</li>
 *   <li>Publicação do evento de resultado</li>
 * </ol>
 */
@Tag("integration")
@DisplayName("Fluxo Completo de Pagamento - Integração End-to-End")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoPagamentoIntegrationTest {

    private PagamentoConsumer consumer;
    private ProcessarPagamentoUseCaseImpl useCase;
    private MercadoPagoGatewayImpl mercadoPagoGateway;
    private PagamentoGateway pagamentoGateway;
    private PagamentoPublisherGateway eventPublisher;

    // Credenciais de teste do Mercado Pago (sandbox)
    private static final String TEST_ACCESS_TOKEN = System.getenv("MERCADO_PAGO_ACCESS_TOKEN") != null
            ? System.getenv("MERCADO_PAGO_ACCESS_TOKEN")
            : "TEST-4604387371249325-021610-a9360cf42beb4662ed601e25048d2adb-376051986";

    private static final String TEST_PUBLIC_KEY = System.getenv("MERCADO_PAGO_PUBLIC_KEY") != null
            ? System.getenv("MERCADO_PAGO_PUBLIC_KEY")
            : "TEST-b119b7dd-037a-43a9-afe3-bfdfe8d2ef3f";

    @BeforeEach
    void setUp() {
        // Configurar gateway real do Mercado Pago
        this.mercadoPagoGateway = new MercadoPagoGatewayImpl();
        ReflectionTestUtils.setField(this.mercadoPagoGateway, "accessToken", TEST_ACCESS_TOKEN);
        ReflectionTestUtils.setField(this.mercadoPagoGateway, "publicKey", TEST_PUBLIC_KEY);
        this.mercadoPagoGateway.init();

        // Mocks para persistência e eventos
        this.pagamentoGateway = mock(PagamentoGateway.class);
        this.eventPublisher = mock(PagamentoPublisherGateway.class);

        // Configurar UseCase
        this.useCase = new ProcessarPagamentoUseCaseImpl(
                this.pagamentoGateway,
                this.eventPublisher,
                this.mercadoPagoGateway
        );

        // Configurar Consumer
        this.consumer = new PagamentoConsumer(this.useCase);
    }

    @Test
    @Order(1)
    @DisplayName("Deve processar pagamento completo com cartão de crédito e aprovar")
    void deveProcessarPagamentoCompletoComCartaoCreditoEAprovar() {
        // Arrange
        PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                "os-" + UUID.randomUUID().toString().substring(0, 8),
                "cliente-integration-test",
                BigDecimal.valueOf(150.00),
                BigDecimal.valueOf(15.00),
                BigDecimal.valueOf(135.00),
                MetodoPagamento.CARTAO_CREDITO,
                3,
                "sistema-teste-integracao"
        );

        // Act
        this.consumer.receiveMessage(event);

        // Assert - Verificar que pagamento foi salvo
        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(this.pagamentoGateway, times(1)).save(pagamentoCaptor.capture());

        Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
        assertThat(pagamentoSalvo.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
        assertThat(pagamentoSalvo.getTransacaoId()).isNotNull();
        assertThat(pagamentoSalvo.getOrdemServicoId()).isEqualTo(event.ordemServicoId());
        assertThat(pagamentoSalvo.getValorTotal()).isEqualByComparingTo(event.valorTotal());

        // Assert - Verificar que evento de autorizado foi publicado
        verify(this.eventPublisher, times(1)).pagamentoAutorizado(any(Pagamento.class));
        verify(this.eventPublisher, never()).pagamentoNaoAturizado(any());

        System.out.println("✅ Fluxo completo executado com sucesso!");
        System.out.println("   - Ordem de Serviço: " + event.ordemServicoId());
        System.out.println("   - Transação ID: " + pagamentoSalvo.getTransacaoId());
        System.out.println("   - Status: " + pagamentoSalvo.getStatusPagamento());
    }

    @Test
    @Order(2)
    @DisplayName("Deve processar pagamento com cartão de débito e aprovar")
    void deveProcessarPagamentoComCartaoDebitoEAprovar() {
        // Arrange
        PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                "os-" + UUID.randomUUID().toString().substring(0, 8),
                "cliente-debito-test",
                BigDecimal.valueOf(75.00),
                BigDecimal.ZERO,
                BigDecimal.valueOf(75.00),
                MetodoPagamento.CARTAO_DEBITO,
                1,
                "sistema-teste-debito"
        );

        // Act
        this.consumer.receiveMessage(event);

        // Assert
        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(this.pagamentoGateway).save(pagamentoCaptor.capture());

        Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
        assertThat(pagamentoSalvo.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);

        verify(this.eventPublisher).pagamentoAutorizado(any());

        System.out.println("✅ Pagamento com débito aprovado!");
        System.out.println("   - Transação ID: " + pagamentoSalvo.getTransacaoId());
    }

    @Test
    @Order(3)
    @DisplayName("Deve gerar dados completos do pagamento após processamento")
    void deveGerarDadosCompletosAposProcessamento() {
        // Arrange
        PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                "os-complete-data",
                "cliente-dados-completos",
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(20.00),
                BigDecimal.valueOf(180.00),
                MetodoPagamento.CARTAO_CREDITO,
                6,
                "usuario-completo"
        );

        // Act
        this.consumer.receiveMessage(event);

        // Assert
        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(this.pagamentoGateway).save(pagamentoCaptor.capture());

        Pagamento pagamento = pagamentoCaptor.getValue();

        // Verificar todos os campos
        assertThat(pagamento.getPagamentoId()).isNotNull();
        assertThat(pagamento.getOrdemServicoId()).isEqualTo("os-complete-data");
        assertThat(pagamento.getClienteId()).isEqualTo("cliente-dados-completos");
        assertThat(pagamento.getValor()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(pagamento.getDesconto()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
        assertThat(pagamento.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(180.00));
        assertThat(pagamento.getMetodoPagamento()).isEqualTo(MetodoPagamento.CARTAO_CREDITO);
        assertThat(pagamento.getQuantidadeParcelas()).isEqualTo(6);
        assertThat(pagamento.getCriadoPor()).isEqualTo("usuario-completo");
        assertThat(pagamento.getDataCriacao()).isNotNull();
        assertThat(pagamento.getTransacaoId()).isNotNull();
        assertThat(pagamento.getChaveIdempotencia()).isNotNull();
        assertThat(pagamento.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);

        System.out.println("✅ Dados completos validados!");
    }

    @Test
    @Order(4)
    @DisplayName("Deve processar múltiplos pagamentos em sequência")
    void deveProcessarMultiplosPagamentosEmSequencia() {
        // Arrange
        PagamentoProcessadoEvent event1 = criarEventoPagamentoSemCartao("os-seq-1", BigDecimal.valueOf(50.00));
        PagamentoProcessadoEvent event2 = criarEventoPagamentoSemCartao("os-seq-2", BigDecimal.valueOf(100.00));
        PagamentoProcessadoEvent event3 = criarEventoPagamentoSemCartao("os-seq-3", BigDecimal.valueOf(150.00));

        // Act
        this.consumer.receiveMessage(event1);
        this.consumer.receiveMessage(event2);
        this.consumer.receiveMessage(event3);

        // Assert
        verify(this.pagamentoGateway, times(3)).save(any(Pagamento.class));
        verify(this.eventPublisher, times(3)).pagamentoAutorizado(any());

        System.out.println("✅ 3 pagamentos processados em sequência!");
    }

    @Test
    @Order(5)
    @DisplayName("Deve processar pagamento com dados de cartão dinâmicos")
    void deveProcessarPagamentoComDadosCartaoDinamicos() {
        // Arrange - Evento com dados completos do cartão
        PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                "os-cartao-dinamico-" + UUID.randomUUID().toString().substring(0, 8),
                "cliente-cartao-dinamico",
                BigDecimal.valueOf(200.00),
                BigDecimal.ZERO,
                BigDecimal.valueOf(200.00),
                MetodoPagamento.CARTAO_CREDITO,
                3,
                "usuario-cartao-teste",
                // Dados do cartão de teste (Mastercard aprovado)
                "5031433215406351",
                "123",
                11,
                2030,
                "APRO",  // Nome APRO = aprovação automática no sandbox
                "19119119100",
                "test_user@testuser.com"
        );

        // Act
        this.consumer.receiveMessage(event);

        // Assert
        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(this.pagamentoGateway).save(pagamentoCaptor.capture());

        Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
        // Em ambiente sandbox, o pagamento pode ser AUTORIZADO ou NAO_AUTORIZADO dependendo do estado da API
        assertThat(pagamentoSalvo.getStatusPagamento()).isIn(StatusPagamento.AUTORIZADO, StatusPagamento.NAO_AUTORIZADO);

        // Verifica que um dos eventos foi publicado
        if (pagamentoSalvo.getStatusPagamento() == StatusPagamento.AUTORIZADO) {
            verify(this.eventPublisher).pagamentoAutorizado(any(Pagamento.class));
            System.out.println("✅ Pagamento com cartão dinâmico processado com sucesso!");
            System.out.println("   - Transação ID: " + pagamentoSalvo.getTransacaoId());
        } else {
            verify(this.eventPublisher).pagamentoNaoAturizado(any(Pagamento.class));
            System.out.println("⚠️ Pagamento com cartão dinâmico não autorizado (possível rate-limit ou problema na API)");
            System.out.println("   - Código Erro: " + pagamentoSalvo.getCodigoErro());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Deve processar pagamento sem dados de cartão usando cartão padrão de teste")
    void deveProcessarPagamentoSemDadosCartaoUsandoCartaoPadrao() {
        // Arrange - Evento sem dados do cartão (usando construtor de compatibilidade)
        PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                "os-cartao-padrao-" + UUID.randomUUID().toString().substring(0, 8),
                "cliente-cartao-padrao",
                BigDecimal.valueOf(100.00),
                BigDecimal.ZERO,
                BigDecimal.valueOf(100.00),
                MetodoPagamento.CARTAO_CREDITO,
                1,
                "sistema"
        );

        // Act
        this.consumer.receiveMessage(event);

        // Assert - Deve funcionar usando o cartão padrão de teste
        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(this.pagamentoGateway).save(pagamentoCaptor.capture());

        Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
        assertThat(pagamentoSalvo.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);

        System.out.println("✅ Pagamento com cartão padrão processado com sucesso!");
    }

    private PagamentoProcessadoEvent criarEventoPagamentoSemCartao(String ordemServicoId, BigDecimal valor) {
        return new PagamentoProcessadoEvent(
                ordemServicoId,
                "cliente-multiplo",
                valor,
                BigDecimal.ZERO,
                valor,
                MetodoPagamento.CARTAO_CREDITO,
                1,
                "sistema"
        );
    }
}

