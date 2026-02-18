package com.fiap.pj.core.pagamento.app;

import com.fiap.pj.core.pagamento.app.gateways.MercadoPagoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.MetodoPagamento;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ProcessarPagamentoUseCaseImpl")
class ProcessarPagamentoUseCaseImplTest {

    private PagamentoGateway pagamentoGateway;
    private PagamentoPublisherGateway eventPublisher;
    private MercadoPagoGateway mercadoPagoGateway;
    private ProcessarPagamentoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        this.pagamentoGateway = mock(PagamentoGateway.class);
        this.eventPublisher = mock(PagamentoPublisherGateway.class);
        this.mercadoPagoGateway = mock(MercadoPagoGateway.class);
        this.useCase = new ProcessarPagamentoUseCaseImpl(
                this.pagamentoGateway,
                this.eventPublisher,
                this.mercadoPagoGateway
        );
    }

    @Nested
    @DisplayName("Quando pagamento é autorizado")
    class QuandoPagamentoAutorizado {

        @Test
        @DisplayName("Deve processar, salvar e publicar evento de pagamento autorizado")
        void deveProcessarSalvarEPublicarEventoAutorizado() {
            // Arrange
            ProcessarPagamentoCommand cmd = criarComandoPagamento();

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setStatusPagamento(StatusPagamento.AUTORIZADO);
                p.setTransacaoId("trans-123");
                p.setCodigoAutorizacao("AUTH-456");
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoGateway, times(1)).save(pagamentoCaptor.capture());

            Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
            assertThat(pagamentoSalvo.getOrdemServicoId()).isEqualTo(cmd.getOrdemServicoId());
            assertThat(pagamentoSalvo.getClienteId()).isEqualTo(cmd.getClienteId());
            assertThat(pagamentoSalvo.getValorTotal()).isEqualByComparingTo(cmd.getValorTotal());
            assertThat(pagamentoSalvo.getStatusPagamento()).isEqualTo(StatusPagamento.AUTORIZADO);
            assertThat(pagamentoSalvo.getTransacaoId()).isEqualTo("trans-123");

            verify(eventPublisher, times(1)).pagamentoAutorizado(pagamentoSalvo);
            verify(eventPublisher, never()).pagamentoNaoAturizado(any());
        }

        @Test
        @DisplayName("Deve gerar pagamentoId único")
        void deveGerarPagamentoIdUnico() {
            // Arrange
            ProcessarPagamentoCommand cmd = criarComandoPagamento();

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setStatusPagamento(StatusPagamento.AUTORIZADO);
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoGateway).save(pagamentoCaptor.capture());

            Pagamento pagamento = pagamentoCaptor.getValue();
            assertThat(pagamento.getPagamentoId()).isNotNull();
            assertThat(pagamento.getPagamentoId()).isNotEmpty();
        }

        @Test
        @DisplayName("Deve definir data de criação")
        void deveDefinirDataCriacao() {
            // Arrange
            ProcessarPagamentoCommand cmd = criarComandoPagamento();

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setStatusPagamento(StatusPagamento.AUTORIZADO);
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoGateway).save(pagamentoCaptor.capture());

            Pagamento pagamento = pagamentoCaptor.getValue();
            assertThat(pagamento.getDataCriacao()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Quando pagamento não é autorizado")
    class QuandoPagamentoNaoAutorizado {

        @Test
        @DisplayName("Deve processar, salvar e publicar evento de pagamento não autorizado")
        void deveProcessarSalvarEPublicarEventoNaoAutorizado() {
            // Arrange
            ProcessarPagamentoCommand cmd = criarComandoPagamento();

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setStatusPagamento(StatusPagamento.NAO_AUTORIZADO);
                p.setCodigoErro("CARD_REJECTED");
                p.setMensagemErro("Cartão rejeitado pelo emissor");
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoGateway, times(1)).save(pagamentoCaptor.capture());

            Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
            assertThat(pagamentoSalvo.getStatusPagamento()).isEqualTo(StatusPagamento.NAO_AUTORIZADO);
            assertThat(pagamentoSalvo.getCodigoErro()).isEqualTo("CARD_REJECTED");
            assertThat(pagamentoSalvo.getMensagemErro()).isEqualTo("Cartão rejeitado pelo emissor");

            verify(eventPublisher, times(1)).pagamentoNaoAturizado(pagamentoSalvo);
            verify(eventPublisher, never()).pagamentoAutorizado(any());
        }

        @Test
        @DisplayName("Deve publicar evento não autorizado para status PENDENTE")
        void devePublicarEventoNaoAutorizadoParaStatusPendente() {
            // Arrange
            ProcessarPagamentoCommand cmd = criarComandoPagamento();

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setStatusPagamento(StatusPagamento.PENDENTE);
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            verify(eventPublisher, times(1)).pagamentoNaoAturizado(any());
            verify(eventPublisher, never()).pagamentoAutorizado(any());
        }

        @Test
        @DisplayName("Deve publicar evento não autorizado para status CANCELADO")
        void devePublicarEventoNaoAutorizadoParaStatusCancelado() {
            // Arrange
            ProcessarPagamentoCommand cmd = criarComandoPagamento();

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setStatusPagamento(StatusPagamento.CANCELADO);
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            verify(eventPublisher, times(1)).pagamentoNaoAturizado(any());
            verify(eventPublisher, never()).pagamentoAutorizado(any());
        }
    }

    @Nested
    @DisplayName("Validação de dados do comando")
    class ValidacaoDadosComando {

        @Test
        @DisplayName("Deve mapear todos os dados do comando para o pagamento")
        void devMapearTodosDadosDoComandoParaPagamento() {
            // Arrange
            ProcessarPagamentoCommand cmd = new ProcessarPagamentoCommand(
                    "os-999",
                    "cliente-888",
                    BigDecimal.valueOf(200),
                    BigDecimal.valueOf(20),
                    BigDecimal.valueOf(180),
                    MetodoPagamento.CARTAO_DEBITO,
                    6,
                    "usuario-teste"
            );

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setStatusPagamento(StatusPagamento.AUTORIZADO);
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
            verify(pagamentoGateway).save(pagamentoCaptor.capture());

            Pagamento pagamento = pagamentoCaptor.getValue();
            assertThat(pagamento.getOrdemServicoId()).isEqualTo("os-999");
            assertThat(pagamento.getClienteId()).isEqualTo("cliente-888");
            assertThat(pagamento.getValor()).isEqualByComparingTo(BigDecimal.valueOf(200));
            assertThat(pagamento.getDesconto()).isEqualByComparingTo(BigDecimal.valueOf(20));
            assertThat(pagamento.getValorTotal()).isEqualByComparingTo(BigDecimal.valueOf(180));
            assertThat(pagamento.getMetodoPagamento()).isEqualTo(MetodoPagamento.CARTAO_DEBITO);
            assertThat(pagamento.getQuantidadeParcelas()).isEqualTo(6);
            assertThat(pagamento.getCriadoPor()).isEqualTo("usuario-teste");
        }

        @Test
        @DisplayName("Deve iniciar pagamento com status PENDENTE")
        void deveIniciarPagamentoComStatusPendente() {
            // Arrange
            ProcessarPagamentoCommand cmd = criarComandoPagamento();

            when(mercadoPagoGateway.processarPagamento(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                // Verifica se o status inicial era PENDENTE
                assertThat(p.getStatusPagamento()).isEqualTo(StatusPagamento.PENDENTE);
                p.setStatusPagamento(StatusPagamento.AUTORIZADO);
                return p;
            });

            // Act
            useCase.handle(cmd);

            // Assert
            verify(mercadoPagoGateway, times(1)).processarPagamento(any());
        }
    }

    private ProcessarPagamentoCommand criarComandoPagamento() {
        return new ProcessarPagamentoCommand(
                "os-123",
                "cliente-1",
                BigDecimal.valueOf(100),
                BigDecimal.TEN,
                BigDecimal.valueOf(90),
                MetodoPagamento.CARTAO_CREDITO,
                3,
                "sistema-teste"
        );
    }
}
