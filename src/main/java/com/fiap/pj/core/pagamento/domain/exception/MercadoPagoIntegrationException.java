package com.fiap.pj.core.pagamento.domain.exception;

/**
 * Exceção específica para erros na integração com o Mercado Pago.
 */
public class MercadoPagoIntegrationException extends PagamentoException {

    private final int statusCode;
    private final String responseBody;

    public MercadoPagoIntegrationException(String mensagem) {
        super("MP_INTEGRATION_ERROR", mensagem);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public MercadoPagoIntegrationException(String mensagem, Throwable causa) {
        super("MP_INTEGRATION_ERROR", mensagem, causa);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public MercadoPagoIntegrationException(int statusCode, String responseBody) {
        super("MP_API_ERROR", "Erro na API do Mercado Pago. Status: " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}

