package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.app.gateways.MercadoPagoGateway;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.fiap.pj.core.pagamento.domain.exception.MercadoPagoIntegrationException;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

/**
 * Implementação do gateway de integração com o Mercado Pago.
 *
 * <p>Esta classe é responsável por processar pagamentos via cartão de crédito/débito
 * utilizando a API do Mercado Pago em ambiente sandbox.</p>
 *
 * <p>Para ambiente de teste, utiliza cartão de teste Mastercard com aprovação automática.</p>
 */
@Slf4j
@Component
public class MercadoPagoGatewayImpl implements MercadoPagoGateway {

    // Dados do pagador de teste (Sandbox)
    private static final String DEFAULT_SANDBOX_EMAIL = "test_user_123@testuser.com";
    private static final String DEFAULT_SANDBOX_CPF = "19119119100";
    private static final String DEFAULT_SANDBOX_FIRST_NAME = "Test";
    private static final String DEFAULT_SANDBOX_LAST_NAME = "User";

    // Cartão de teste Mastercard (Mercado Pago Sandbox)
    private static final String TEST_CARD_NUMBER = "5031433215406351";
    private static final String TEST_CARD_SECURITY_CODE = "123";
    private static final int TEST_CARD_EXPIRATION_MONTH = 11;
    private static final int TEST_CARD_EXPIRATION_YEAR = 2030;
    private static final String TEST_CARD_HOLDER_NAME = "APRO";  // APRO = Aprovado

    // Configurações de timeout
    private static final int REQUEST_TIMEOUT_MS = 30000;
    private static final int MAX_ERROR_MESSAGE_LENGTH = 500;

    // API endpoints
    private static final String CARD_TOKEN_API_URL = "https://api.mercadopago.com/v1/card_tokens";

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.public-key}")
    private String publicKey;

    private PaymentClient paymentClient;
    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(this.accessToken);
        this.paymentClient = new PaymentClient();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
                .build();
        log.info("MercadoPago Gateway inicializado com sucesso (Modo Sandbox - Cartão de Teste)");
    }

    /**
     * Processa um pagamento via Mercado Pago utilizando cartão de crédito de teste.
     *
     * @param pagamento objeto contendo os dados do pagamento
     * @return pagamento atualizado com status da transação
     */
    @Override
    public Pagamento processarPagamento(Pagamento pagamento) {
        log.info("Iniciando processamento de pagamento no Mercado Pago. OS: {}, Método: {}, Valor: {}",
                pagamento.getOrdemServicoId(),
                pagamento.getMetodoPagamento(),
                pagamento.getValorTotal());

        this.garantirChaveIdempotencia(pagamento);

        try {
            String cardToken = this.gerarTokenCartaoTeste();

            PaymentCreateRequest request = this.criarRequisicaoPagamentoCartao(pagamento, cardToken);
            MPRequestOptions options = this.configurarRequisicaoIntegracao(pagamento.getChaveIdempotencia());

            Payment response = this.executarPagamento(request, options);

            this.atualizarPagamentoComResposta(pagamento, response);

            log.info("Pagamento processado com sucesso. TransacaoId: {}, Status: {}, OS: {}",
                    pagamento.getTransacaoId(),
                    pagamento.getStatusPagamento(),
                    pagamento.getOrdemServicoId());

        } catch (MercadoPagoIntegrationException e) {
            log.error("Erro na integração com Mercado Pago. OS: {}, Código: {}, Mensagem: {}", pagamento.getOrdemServicoId(), e.getCodigoErro(), e.getMessage());
            this.atualizarPagamentoComFalha(pagamento, e.getCodigoErro(), e.getMessage());

        } catch (MPApiException e) {
            log.error("Erro na API do Mercado Pago. OS: {}, Status: {}, Mensagem: {}", pagamento.getOrdemServicoId(), e.getStatusCode(), e.getMessage());
            this.atualizarPagamentoComFalha(pagamento, "MP_API_ERROR", e.getMessage());

        } catch (MPException e) {
            log.error("Erro no SDK do Mercado Pago. OS: {}, Mensagem: {}", pagamento.getOrdemServicoId(), e.getMessage());
            this.atualizarPagamentoComFalha(pagamento, "MP_SDK_ERROR", e.getMessage());

        } catch (Exception e) {
            log.error("Erro inesperado ao processar pagamento. OS: {}, Erro: {}", pagamento.getOrdemServicoId(), e.getMessage(), e);
            this.atualizarPagamentoComFalha(pagamento, "MP_UNEXPECTED_ERROR", e.getMessage());
        }

        return pagamento;
    }

    /**
     * Garante que o pagamento tenha uma chave de idempotência.
     */
    private void garantirChaveIdempotencia(Pagamento pagamento) {
        if (StringUtils.isBlank(pagamento.getChaveIdempotencia())) {
            pagamento.setChaveIdempotencia(UUID.randomUUID().toString());
            log.debug("Chave de idempotência gerada: {}", pagamento.getChaveIdempotencia());
        }
    }

    /**
     * Gera token do cartão de teste Mastercard para ambiente sandbox usando API REST.
     *
     * @return ID do token do cartão
     * @throws MercadoPagoIntegrationException se houver erro na geração do token
     */
    private String gerarTokenCartaoTeste() throws MercadoPagoIntegrationException {
        log.debug("Gerando token do cartão de teste Mastercard");

        String jsonBody = this.criarCorpoRequisicaoToken();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CARD_TOKEN_API_URL + "?public_key=" + this.publicKey))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(REQUEST_TIMEOUT_MS))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (!this.isSuccessResponse(response.statusCode())) {
                log.error("Erro ao gerar token do cartão. Status: {}, Body: {}", response.statusCode(), response.body());
                throw new MercadoPagoIntegrationException(response.statusCode(), response.body());
            }

            String tokenId = this.extrairTokenId(response.body());
            log.debug("Token do cartão gerado com sucesso: {}", tokenId);

            return tokenId;
        } catch (MercadoPagoIntegrationException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MercadoPagoIntegrationException("Requisição interrompida ao gerar token do cartão", e);
        } catch (Exception e) {
            throw new MercadoPagoIntegrationException("Erro ao gerar token do cartão", e);
        }
    }

    /**
     * Cria o corpo da requisição para geração do token do cartão.
     */
    private String criarCorpoRequisicaoToken() {
        return String.format("""
            {
                "card_number": "%s",
                "security_code": "%s",
                "expiration_month": %d,
                "expiration_year": %d,
                "cardholder": {
                    "name": "%s",
                    "identification": {
                        "type": "CPF",
                        "number": "%s"
                    }
                }
            }
            """,
            TEST_CARD_NUMBER,
            TEST_CARD_SECURITY_CODE,
            TEST_CARD_EXPIRATION_MONTH,
            TEST_CARD_EXPIRATION_YEAR,
            TEST_CARD_HOLDER_NAME,
            DEFAULT_SANDBOX_CPF
        );
    }

    /**
     * Verifica se o código de status HTTP indica sucesso.
     */
    private boolean isSuccessResponse(int statusCode) {
        return statusCode == 200 || statusCode == 201;
    }

    /**
     * Extrai o ID do token da resposta JSON do Mercado Pago.
     */
    private String extrairTokenId(String jsonResponse) {
        int idIndex = jsonResponse.indexOf("\"id\"");
        if (idIndex == -1) {
            throw new MercadoPagoIntegrationException("Token ID não encontrado na resposta");
        }

        int colonIndex = jsonResponse.indexOf(":", idIndex);
        int startQuote = jsonResponse.indexOf("\"", colonIndex);
        int endQuote = jsonResponse.indexOf("\"", startQuote + 1);

        return jsonResponse.substring(startQuote + 1, endQuote);
    }

    /**
     * Cria a requisição de pagamento com cartão de crédito.
     */
    private PaymentCreateRequest criarRequisicaoPagamentoCartao(Pagamento pagamento, String cardToken) {
        return PaymentCreateRequest.builder()
                .transactionAmount(pagamento.getValorTotal())
                .description("Pagamento Ordem de Servico: " + pagamento.getOrdemServicoId())
                .paymentMethodId("master")
                .token(cardToken)
                .installments(this.obterQuantidadeParcelas(pagamento))
                .payer(this.criarPayerRequest())
                .externalReference(pagamento.getPagamentoId())
                .build();
    }

    /**
     * Obtém a quantidade de parcelas, utilizando 1 como padrão.
     */
    private int obterQuantidadeParcelas(Pagamento pagamento) {
        return pagamento.getQuantidadeParcelas() != null ? pagamento.getQuantidadeParcelas() : 1;
    }

    /**
     * Cria o objeto de dados do pagador para a requisição.
     */
    private PaymentPayerRequest criarPayerRequest() {
        return PaymentPayerRequest.builder()
                .email(DEFAULT_SANDBOX_EMAIL)
                .firstName(DEFAULT_SANDBOX_FIRST_NAME)
                .lastName(DEFAULT_SANDBOX_LAST_NAME)
                .identification(com.mercadopago.client.common.IdentificationRequest.builder()
                        .type("CPF")
                        .number(DEFAULT_SANDBOX_CPF)
                        .build())
                .build();
    }

    /**
     * Configura as opções da requisição de integração.
     */
    private MPRequestOptions configurarRequisicaoIntegracao(String idempotencyKey) {
        return MPRequestOptions.builder()
                .connectionTimeout(REQUEST_TIMEOUT_MS)
                .connectionRequestTimeout(REQUEST_TIMEOUT_MS)
                .socketTimeout(REQUEST_TIMEOUT_MS)
                .customHeaders(Map.of("X-Idempotency-Key", idempotencyKey))
                .build();
    }

    /**
     * Executa o pagamento via SDK do Mercado Pago.
     */
    private Payment executarPagamento(PaymentCreateRequest request, MPRequestOptions options) throws MPException, MPApiException {
        return this.paymentClient.create(request, options);
    }

    /**
     * Atualiza o pagamento com os dados da resposta do Mercado Pago.
     */
    private void atualizarPagamentoComResposta(Pagamento pagamento, Payment response) {
        pagamento.setTransacaoId(response.getId().toString());
        pagamento.setStatusPagamento(this.mapearStatusMercadoPago(response.getStatus()));

        if (nonNull(response.getAuthorizationCode())) {
            pagamento.setCodigoAutorizacao(response.getAuthorizationCode());
        }
    }

    /**
     * Atualiza o pagamento com informações de falha.
     */
    private void atualizarPagamentoComFalha(Pagamento pagamento, String codigoErro, String mensagem) {
        pagamento.setStatusPagamento(StatusPagamento.NAO_AUTORIZADO);
        pagamento.setCodigoErro(codigoErro);
        pagamento.setMensagemErro(this.truncateMessage(mensagem));
    }

    /**
     * Mapeia o status retornado pelo Mercado Pago para o status interno.
     */
    private StatusPagamento mapearStatusMercadoPago(String status) {
        if (status == null) {
            log.warn("Status de pagamento retornado pelo Mercado Pago é nulo");
            return StatusPagamento.NAO_AUTORIZADO;
        }

        return switch (status.toLowerCase()) {
            case "approved" -> StatusPagamento.AUTORIZADO;
            case "pending", "in_process", "authorized" -> StatusPagamento.PENDENTE;
            case "rejected" -> StatusPagamento.NAO_AUTORIZADO;
            case "cancelled" -> StatusPagamento.CANCELADO;
            case "refunded", "charged_back" -> StatusPagamento.ESTORNADO;
            default -> {
                log.warn("Status de pagamento desconhecido: {}", status);
                yield StatusPagamento.NAO_AUTORIZADO;
            }
        };
    }

    /**
     * Trunca mensagem de erro para evitar problemas de armazenamento.
     */
    private String truncateMessage(String message) {
        if (message == null) {
            return "Erro desconhecido";
        }
        return message.length() > MAX_ERROR_MESSAGE_LENGTH
                ? message.substring(0, MAX_ERROR_MESSAGE_LENGTH)
                : message;
    }
}
