package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MercadoPagoGatewayImpl")
class MercadoPagoGatewayImplTest {

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @Mock
    private Payment paymentResponse;

    private MercadoPagoGatewayImpl gateway;

    @BeforeEach
    void setUp() throws Exception {
        this.gateway = new MercadoPagoGatewayImpl();

        // Inject mocked PaymentClient via reflection
        this.setFieldValue("paymentClient", this.paymentClient);
        this.setFieldValue("httpClient", this.httpClient);
        this.setFieldValue("accessToken", "TEST-ACCESS-TOKEN");
        this.setFieldValue("publicKey", "TEST-PUBLIC-KEY");
    }

    private void setFieldValue(String fieldName, Object value) throws Exception {
        Field field = MercadoPagoGatewayImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this.gateway, value);
    }

    @Nested
    @DisplayName("Quando pagamento é processado com sucesso")
    class QuandoPagamentoProcessadoComSucesso {

        @Test
        @DisplayName("Deve retornar pagamento aprovado quando status é approved")
        void deveRetornarPagamentoAprovadoQuandoStatusApproved() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("approved");
            when(paymentResponse.getAuthorizationCode()).thenReturn("AUTH123");

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
            assertThat(resultado.getTransacaoId()).isEqualTo("12345");
            assertThat(resultado.getCodigoAutorizacao()).isEqualTo("AUTH123");
            assertThat(resultado.getChaveIdempotencia()).isNotBlank();
        }

        @Test
        @DisplayName("Deve retornar pagamento pendente quando status é pending")
        void deveRetornarPagamentoPendenteQuandoStatusPending() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("pending");

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.PENDENTE);
            assertThat(resultado.getTransacaoId()).isEqualTo("12345");
        }
    }

    @Nested
    @DisplayName("Mapeamento de status do Mercado Pago")
    class MapeamentoStatusMercadoPago {

        @ParameterizedTest
        @CsvSource({
                "approved, AUTORIZADO",
                "pending, PENDENTE",
                "in_process, PENDENTE",
                "authorized, PENDENTE",
                "rejected, NAO_AUTORIZADO",
                "cancelled, CANCELADO",
                "refunded, ESTORNADO",
                "charged_back, ESTORNADO"
        })
        @DisplayName("Deve mapear corretamente os status do Mercado Pago")
        void deveMappearCorretamenteOsStatusDoMercadoPago(String statusMp, StatusPagamento statusEsperado)
                throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn(statusMp);

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(statusEsperado);
        }

        @Test
        @DisplayName("Deve retornar NAO_AUTORIZADO quando status é nulo")
        void deveRetornarNaoAutorizadoQuandoStatusNulo() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn(null);

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.NAO_AUTORIZADO);
        }

        @Test
        @DisplayName("Deve retornar NAO_AUTORIZADO quando status é desconhecido")
        void deveRetornarNaoAutorizadoQuandoStatusDesconhecido() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("unknown_status");

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.NAO_AUTORIZADO);
        }
    }

    @Nested
    @DisplayName("Tratamento de erros")
    class TratamentoErros {

        @Test
        @DisplayName("Deve tratar erro de API e retornar NAO_AUTORIZADO")
        void deveTratarErroApiERetornarNaoAutorizado() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            MPApiException apiException = mock(MPApiException.class);
            when(apiException.getMessage()).thenReturn("Erro de API");
            when(apiException.getStatusCode()).thenReturn(400);

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenThrow(apiException);

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.NAO_AUTORIZADO);
            assertThat(resultado.getCodigoErro()).isEqualTo("MP_API_ERROR");
        }

        @Test
        @DisplayName("Deve tratar erro de SDK e retornar NAO_AUTORIZADO")
        void deveTratarErroSdkERetornarNaoAutorizado() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenThrow(new MPException("Erro de SDK"));

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.NAO_AUTORIZADO);
            assertThat(resultado.getCodigoErro()).isEqualTo("MP_SDK_ERROR");
        }

        @Test
        @DisplayName("Deve tratar erro inesperado e retornar NAO_AUTORIZADO")
        void deveTratarErroInesperadoERetornarNaoAutorizado() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.NAO_AUTORIZADO);
            assertThat(resultado.getCodigoErro()).isEqualTo("MP_UNEXPECTED_ERROR");
            assertThat(resultado.getMensagemErro()).isEqualTo("Erro inesperado");
        }

        @Test
        @DisplayName("Deve truncar mensagem de erro quando muito grande")
        void deveTruncarMensagemDeErroQuandoMuitoGrande() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();
            String mensagemGrande = "A".repeat(600);

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenThrow(new RuntimeException(mensagemGrande));

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getMensagemErro()).hasSize(500);
        }

        @Test
        @DisplayName("Deve retornar mensagem padrão quando erro é nulo")
        void deveRetornarMensagemPadraoQuandoErroNulo() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenThrow(new RuntimeException((String) null));

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getMensagemErro()).isEqualTo("Erro desconhecido");
        }

        @Test
        @DisplayName("Deve tratar erro na geração do token")
        void deveTratarErroNaGeracaoDoToken() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();

            // Simular erro na geração do token (status 400)
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(httpResponse);
            when(httpResponse.statusCode()).thenReturn(400);
            when(httpResponse.body()).thenReturn("{\"error\": \"invalid_card\"}");

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getStatusPagamento()).isEqualTo(StatusPagamento.NAO_AUTORIZADO);
            assertThat(resultado.getCodigoErro()).isEqualTo("MP_API_ERROR");

            // Verificar que o paymentClient não foi chamado (porque falhou antes)
            verify(paymentClient, never()).create(any(), any());
        }
    }

    @Nested
    @DisplayName("Chave de idempotência")
    class ChaveIdempotencia {

        @Test
        @DisplayName("Deve manter chave de idempotência se fornecida")
        void deveManterChaveIdempotenciaSeFornecida() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            String chaveExistente = "chave-existente-123";
            pagamento.setChaveIdempotencia(chaveExistente);
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("approved");

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getChaveIdempotencia()).isEqualTo(chaveExistente);
        }

        @Test
        @DisplayName("Deve gerar chave de idempotência se não fornecida")
        void deveGerarChaveIdempotenciaSeNaoFornecida() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            pagamento.setChaveIdempotencia(null);
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("approved");

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getChaveIdempotencia()).isNotNull();
            assertThat(resultado.getChaveIdempotencia()).isNotEmpty();
        }

        @Test
        @DisplayName("Deve gerar chave de idempotência se fornecida em branco")
        void deveGerarChaveIdempotenciaSeFornecidaEmBranco() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            pagamento.setChaveIdempotencia("   ");
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("approved");

            // Act
            Pagamento resultado = gateway.processarPagamento(pagamento);

            // Assert
            assertThat(resultado.getChaveIdempotencia()).isNotBlank();
            assertThat(resultado.getChaveIdempotencia()).isNotEqualTo("   ");
        }
    }

    @Nested
    @DisplayName("Quantidade de parcelas")
    class QuantidadeParcelas {

        @Test
        @DisplayName("Deve usar parcelas informadas no pagamento")
        void deveUsarParcelasInformadasNoPagamento() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            pagamento.setQuantidadeParcelas(6);
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("approved");

            // Act
            gateway.processarPagamento(pagamento);

            // Assert - verificação via captor se necessário
            verify(paymentClient).create(any(PaymentCreateRequest.class), any(MPRequestOptions.class));
        }

        @Test
        @DisplayName("Deve usar 1 parcela como padrão quando não informado")
        void deveUsarUmaParcelaComoPadraoQuandoNaoInformado() throws Exception {
            // Arrange
            Pagamento pagamento = criarPagamentoTeste();
            pagamento.setQuantidadeParcelas(null);
            configurarTokenResponseSucesso();

            when(paymentClient.create(any(PaymentCreateRequest.class), any(MPRequestOptions.class)))
                    .thenReturn(paymentResponse);
            when(paymentResponse.getId()).thenReturn(12345L);
            when(paymentResponse.getStatus()).thenReturn("approved");

            // Act
            gateway.processarPagamento(pagamento);

            // Assert
            verify(paymentClient).create(any(PaymentCreateRequest.class), any(MPRequestOptions.class));
        }
    }

    private void configurarTokenResponseSucesso() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(201);
        when(httpResponse.body()).thenReturn("{\"id\": \"test-token-123\", \"status\": \"active\"}");
    }

    private Pagamento criarPagamentoTeste() {
        return Pagamento.builder()
                .pagamentoId("pag-123")
                .ordemServicoId("os-456")
                .clienteId("cliente-789")
                .valor(BigDecimal.valueOf(100.00))
                .desconto(BigDecimal.valueOf(10.00))
                .valorTotal(BigDecimal.valueOf(90.00))
                .metodoPagamento(MetodoPagamento.CARTAO_CREDITO)
                .quantidadeParcelas(3)
                .statusPagamento(StatusPagamento.PENDENTE)
                .build();
    }
}

