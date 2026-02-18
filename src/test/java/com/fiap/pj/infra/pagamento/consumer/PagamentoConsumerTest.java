package com.fiap.pj.infra.pagamento.consumer;

import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.event.PagamentoProcessadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PagamentoConsumer")
class PagamentoConsumerTest {

    private ProcessarPagamentoUseCase processarPagamentoUseCase;
    private PagamentoConsumer consumer;

    @BeforeEach
    void setUp() {
        this.processarPagamentoUseCase = mock(ProcessarPagamentoUseCase.class);
        this.consumer = new PagamentoConsumer(this.processarPagamentoUseCase);
    }

    @Nested
    @DisplayName("Recepção de mensagens")
    class RecepcaoMensagens {

        @Test
        @DisplayName("Deve processar evento de pagamento recebido da fila")
        void deveProcessarEventoPagamentoRecebidoDaFila() {
            // Arrange
            PagamentoProcessadoEvent event = criarEventoPagamentoSemCartao();

            // Act
            consumer.receiveMessage(event);

            // Assert
            verify(processarPagamentoUseCase, times(1)).handle(any());
        }

        @Test
        @DisplayName("Deve mapear corretamente os dados do evento para o comando")
        void deveMapearCorretamenteDadosDoEventoParaComando() {
            // Arrange
            PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                    "os-123",
                    "cliente-456",
                    BigDecimal.valueOf(100),
                    BigDecimal.valueOf(10),
                    BigDecimal.valueOf(90),
                    MetodoPagamento.CARTAO_CREDITO,
                    3,
                    "usuario-teste"
            );

            // Act
            consumer.receiveMessage(event);

            // Assert
            verify(processarPagamentoUseCase).handle(argThat(cmd ->
                    cmd.getOrdemServicoId().equals("os-123") &&
                    cmd.getClienteId().equals("cliente-456") &&
                    cmd.getValor().compareTo(BigDecimal.valueOf(100)) == 0 &&
                    cmd.getDesconto().compareTo(BigDecimal.valueOf(10)) == 0 &&
                    cmd.getValorTotal().compareTo(BigDecimal.valueOf(90)) == 0 &&
                    cmd.getMetodoPagamento() == MetodoPagamento.CARTAO_CREDITO &&
                    cmd.getQuantidadeParcelas() == 3 &&
                    cmd.getUsuarioId().equals("usuario-teste") &&
                    cmd.getDadosCartao() == null // Sem dados de cartão no evento legado
            ));
        }

        @Test
        @DisplayName("Deve processar evento com cartão de débito")
        void deveProcessarEventoComCartaoDebito() {
            // Arrange
            PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                    "os-789",
                    "cliente-012",
                    BigDecimal.valueOf(50),
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(50),
                    MetodoPagamento.CARTAO_DEBITO,
                    1,
                    "sistema"
            );

            // Act
            consumer.receiveMessage(event);

            // Assert
            verify(processarPagamentoUseCase).handle(argThat(cmd ->
                    cmd.getMetodoPagamento() == MetodoPagamento.CARTAO_DEBITO
            ));
        }

        @Test
        @DisplayName("Deve processar evento sem parcelas (valor nulo)")
        void deveProcessarEventoSemParcelas() {
            // Arrange
            PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                    "os-999",
                    "cliente-888",
                    BigDecimal.valueOf(200),
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(200),
                    MetodoPagamento.CARTAO_CREDITO,
                    null,
                    "usuario"
            );

            // Act
            consumer.receiveMessage(event);

            // Assert
            verify(processarPagamentoUseCase).handle(argThat(cmd ->
                    cmd.getQuantidadeParcelas() == null
            ));
        }

        @Test
        @DisplayName("Deve mapear dados do cartão quando presentes no evento")
        void deveMapearDadosCartaoQuandoPresentesNoEvento() {
            // Arrange
            PagamentoProcessadoEvent event = new PagamentoProcessadoEvent(
                    "os-cartao",
                    "cliente-cartao",
                    BigDecimal.valueOf(150),
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(150),
                    MetodoPagamento.CARTAO_CREDITO,
                    3,
                    "usuario-teste",
                    "5031433215406351",
                    "123",
                    11,
                    2030,
                    "JOAO DA SILVA",
                    "19119119100",
                    "joao@email.com"
            );

            // Act
            consumer.receiveMessage(event);

            // Assert
            verify(processarPagamentoUseCase).handle(argThat(cmd ->
                    cmd.getDadosCartao() != null &&
                    cmd.getDadosCartao().getNumeroCartao().equals("5031433215406351") &&
                    cmd.getDadosCartao().getCodigoSeguranca().equals("123") &&
                    cmd.getDadosCartao().getMesExpiracao() == 11 &&
                    cmd.getDadosCartao().getAnoExpiracao() == 2030 &&
                    cmd.getDadosCartao().getNomeTitular().equals("JOAO DA SILVA") &&
                    cmd.getDadosCartao().getCpfTitular().equals("19119119100") &&
                    cmd.getDadosCartao().getEmailTitular().equals("joao@email.com")
            ));
        }

        @Test
        @DisplayName("Deve retornar dados cartão nulo quando evento não possui dados do cartão")
        void deveRetornarDadosCartaoNuloQuandoEventoSemDadosCartao() {
            // Arrange
            PagamentoProcessadoEvent event = criarEventoPagamentoSemCartao();

            // Act
            consumer.receiveMessage(event);

            // Assert
            verify(processarPagamentoUseCase).handle(argThat(cmd ->
                    cmd.getDadosCartao() == null
            ));
        }
    }

    private PagamentoProcessadoEvent criarEventoPagamentoSemCartao() {
        return new PagamentoProcessadoEvent(
                "os-test",
                "cliente-test",
                BigDecimal.valueOf(100),
                BigDecimal.TEN,
                BigDecimal.valueOf(90),
                MetodoPagamento.CARTAO_CREDITO,
                1,
                "sistema-teste"
        );
    }
}

