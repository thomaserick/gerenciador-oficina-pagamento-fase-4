package com.fiap.pj.core.pagamento.exception;

public class PagamentoExceptions {
    private PagamentoExceptions() {
    }

    public static class PagamentoNaoEncontradoException extends RuntimeException {
        public PagamentoNaoEncontradoException() {
            super("Pagamento não encontrado.");
        }
    }


}

