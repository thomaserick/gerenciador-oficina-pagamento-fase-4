package com.fiap.pj.infra.pagamento.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para informações do cartão de crédito/débito na API de teste.
 *
 * <p><b>IMPORTANTE:</b> Este DTO é apenas para fins de teste em ambiente sandbox.
 * Em produção, os dados do cartão devem ser tokenizados no frontend.</p>
 */
@Schema(description = "Informações do cartão de crédito/débito para teste")
public record DadosCartaoRequest(

        @Schema(description = "Número do cartão (apenas números)", example = "5031433215406351")
        @NotBlank(message = "numeroCartao é obrigatório")
        String numeroCartao,

        @Schema(description = "Código de segurança (CVV)", example = "123")
        @NotBlank(message = "codigoSeguranca é obrigatório")
        String codigoSeguranca,

        @Schema(description = "Mês de expiração (1-12)", example = "11")
        @NotNull(message = "mesExpiracao é obrigatório")
        @Min(value = 1, message = "mesExpiracao deve ser entre 1 e 12")
        @Max(value = 12, message = "mesExpiracao deve ser entre 1 e 12")
        Integer mesExpiracao,

        @Schema(description = "Ano de expiração (formato YYYY)", example = "2030")
        @NotNull(message = "anoExpiracao é obrigatório")
        @Min(value = 2024, message = "anoExpiracao deve ser no mínimo 2024")
        @Max(value = 2050, message = "anoExpiracao deve ser no máximo 2050")
        Integer anoExpiracao,

        @Schema(description = "Nome do titular conforme cartão", example = "APRO")
        @NotBlank(message = "nomeTitular é obrigatório")
        String nomeTitular,

        @Schema(description = "CPF do titular (apenas números)", example = "19119119100")
        @NotBlank(message = "cpfTitular é obrigatório")
        String cpfTitular,

        @Schema(description = "Email do titular", example = "test_user@testuser.com")
        String emailTitular
) {

    /**
     * Retorna o email do titular ou um valor padrão se não informado.
     */
    public String getEmailOuPadrao() {
        return this.emailTitular != null && !this.emailTitular.isBlank()
                ? this.emailTitular
                : "test_user@testuser.com";
    }
}

