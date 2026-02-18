package com.fiap.pj.core.pagamento.domain.exception;

/**
 * Exceção específica para erros no processamento de pagamentos.
 */
public class PagamentoException extends RuntimeException {

    private final String codigoErro;

    public PagamentoException(String mensagem) {
        super(mensagem);
        this.codigoErro = "PAGAMENTO_ERROR";
    }

    public PagamentoException(String codigoErro, String mensagem) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }

    public PagamentoException(String codigoErro, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.codigoErro = codigoErro;
    }

    public String getCodigoErro() {
        return codigoErro;
    }
}

