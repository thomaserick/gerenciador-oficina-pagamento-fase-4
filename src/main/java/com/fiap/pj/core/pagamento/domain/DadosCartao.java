package com.fiap.pj.core.pagamento.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Dados do cartão de crédito/débito para processamento de pagamento.
 *
 * <p>Esta classe encapsula todas as informações necessárias para processar
 * um pagamento com cartão via Mercado Pago.</p>
 *
 * <p><b>IMPORTANTE:</b> Estes dados são sensíveis e não devem ser persistidos.
 * São utilizados apenas durante o processamento do pagamento.</p>
 */
@Getter
@Setter
@Builder
public class DadosCartao {

    /**
     * Número do cartão de crédito/débito.
     * Exemplo: "5031433215406351"
     */
    private String numeroCartao;

    /**
     * Código de segurança (CVV/CVC).
     * Exemplo: "123"
     */
    private String codigoSeguranca;

    /**
     * Mês de expiração do cartão (1-12).
     */
    private Integer mesExpiracao;

    /**
     * Ano de expiração do cartão (formato YYYY).
     */
    private Integer anoExpiracao;

    /**
     * Nome do titular conforme impresso no cartão.
     * Exemplo: "JOAO DA SILVA"
     */
    private String nomeTitular;

    /**
     * CPF do titular do cartão.
     * Exemplo: "19119119100"
     */
    private String cpfTitular;

    /**
     * Email do titular do cartão.
     * Exemplo: "joao@email.com"
     */
    private String emailTitular;

    /**
     * Verifica se os dados do cartão foram preenchidos.
     *
     * @return true se todos os campos obrigatórios estão preenchidos
     */
    public boolean isPreenchido() {
        return this.numeroCartao != null && !this.numeroCartao.isBlank()
                && this.codigoSeguranca != null && !this.codigoSeguranca.isBlank()
                && this.mesExpiracao != null
                && this.anoExpiracao != null
                && this.nomeTitular != null && !this.nomeTitular.isBlank()
                && this.cpfTitular != null && !this.cpfTitular.isBlank();
    }
}

