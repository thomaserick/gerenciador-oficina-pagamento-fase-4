package com.fiap.pj.core.pagamento.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DadosCartao")
class DadosCartaoTest {

    @Nested
    @DisplayName("isPreenchido")
    class IsPreenchido {

        @Test
        @DisplayName("Deve retornar true quando todos os campos obrigatórios estão preenchidos")
        void deveRetornarTrueQuandoTodosCamposPreenchidos() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("5031433215406351")
                    .codigoSeguranca("123")
                    .mesExpiracao(11)
                    .anoExpiracao(2030)
                    .nomeTitular("JOAO DA SILVA")
                    .cpfTitular("19119119100")
                    .emailTitular("joao@email.com")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isTrue();
        }

        @Test
        @DisplayName("Deve retornar true quando email não está preenchido (campo opcional)")
        void deveRetornarTrueQuandoEmailNaoPreenchido() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("5031433215406351")
                    .codigoSeguranca("123")
                    .mesExpiracao(11)
                    .anoExpiracao(2030)
                    .nomeTitular("JOAO DA SILVA")
                    .cpfTitular("19119119100")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando número do cartão é nulo")
        void deveRetornarFalseQuandoNumeroCartaoNulo() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .codigoSeguranca("123")
                    .mesExpiracao(11)
                    .anoExpiracao(2030)
                    .nomeTitular("JOAO DA SILVA")
                    .cpfTitular("19119119100")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando número do cartão está em branco")
        void deveRetornarFalseQuandoNumeroCartaoEmBranco() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("   ")
                    .codigoSeguranca("123")
                    .mesExpiracao(11)
                    .anoExpiracao(2030)
                    .nomeTitular("JOAO DA SILVA")
                    .cpfTitular("19119119100")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando código de segurança é nulo")
        void deveRetornarFalseQuandoCodigoSegurancaNulo() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("5031433215406351")
                    .mesExpiracao(11)
                    .anoExpiracao(2030)
                    .nomeTitular("JOAO DA SILVA")
                    .cpfTitular("19119119100")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando mês de expiração é nulo")
        void deveRetornarFalseQuandoMesExpiracaoNulo() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("5031433215406351")
                    .codigoSeguranca("123")
                    .anoExpiracao(2030)
                    .nomeTitular("JOAO DA SILVA")
                    .cpfTitular("19119119100")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando ano de expiração é nulo")
        void deveRetornarFalseQuandoAnoExpiracaoNulo() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("5031433215406351")
                    .codigoSeguranca("123")
                    .mesExpiracao(11)
                    .nomeTitular("JOAO DA SILVA")
                    .cpfTitular("19119119100")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando nome do titular é nulo")
        void deveRetornarFalseQuandoNomeTitularNulo() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("5031433215406351")
                    .codigoSeguranca("123")
                    .mesExpiracao(11)
                    .anoExpiracao(2030)
                    .cpfTitular("19119119100")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando CPF do titular é nulo")
        void deveRetornarFalseQuandoCpfTitularNulo() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder()
                    .numeroCartao("5031433215406351")
                    .codigoSeguranca("123")
                    .mesExpiracao(11)
                    .anoExpiracao(2030)
                    .nomeTitular("JOAO DA SILVA")
                    .build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando todos os campos são nulos")
        void deveRetornarFalseQuandoTodosCamposNulos() {
            // Arrange
            DadosCartao dadosCartao = DadosCartao.builder().build();

            // Act & Assert
            assertThat(dadosCartao.isPreenchido()).isFalse();
        }
    }
}

