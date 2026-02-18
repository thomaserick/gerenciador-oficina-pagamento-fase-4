package com.fiap.pj.core.pagamento.app;

import com.fiap.pj.core.pagamento.app.gateways.MercadoPagoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.fiap.pj.core.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Implementação do caso de uso para processamento de pagamentos.
 *
 * <p>Este caso de uso é responsável por:</p>
 * <ul>
 *   <li>Criar o objeto de pagamento com os dados recebidos</li>
 *   <li>Processar o pagamento via gateway do Mercado Pago</li>
 *   <li>Persistir o pagamento no banco de dados</li>
 *   <li>Publicar evento de pagamento autorizado ou não autorizado</li>
 * </ul>
 */
@Slf4j
public class ProcessarPagamentoUseCaseImpl implements ProcessarPagamentoUseCase {

    private final PagamentoGateway pagamentoGateway;
    private final PagamentoPublisherGateway eventPublisher;
    private final MercadoPagoGateway mercadoPagoGateway;

    public ProcessarPagamentoUseCaseImpl(PagamentoGateway pagamentoGateway, 
                                         PagamentoPublisherGateway eventPublisher,
                                         MercadoPagoGateway mercadoPagoGateway) {
        this.pagamentoGateway = pagamentoGateway;
        this.eventPublisher = eventPublisher;
        this.mercadoPagoGateway = mercadoPagoGateway;
    }

    /**
     * Processa um comando de pagamento.
     *
     * @param cmd comando contendo os dados do pagamento a ser processado
     */
    @Override
    public void handle(ProcessarPagamentoCommand cmd) {
        log.info("Iniciando processamento de pagamento. OS: {}, Cliente: {}, Valor: {}", cmd.getOrdemServicoId(), cmd.getClienteId(), cmd.getValorTotal());

        Pagamento pagamento = this.criarPagamento(cmd);
        pagamento = this.processarPagamentoNoGateway(pagamento);

        this.salvarPagamento(pagamento);
        this.publicarEvento(pagamento);

        log.info("Processamento de pagamento finalizado. OS: {}, Status: {}", pagamento.getOrdemServicoId(), pagamento.getStatusPagamento());
    }

    /**
     * Cria o objeto de pagamento a partir do comando recebido.
     */
    private Pagamento criarPagamento(ProcessarPagamentoCommand cmd) {
        return Pagamento.builder()
                .pagamentoId(UUID.randomUUID().toString())
                .ordemServicoId(cmd.getOrdemServicoId())
                .clienteId(cmd.getClienteId())
                .valor(cmd.getValor())
                .desconto(cmd.getDesconto())
                .valorTotal(cmd.getValorTotal())
                .metodoPagamento(cmd.getMetodoPagamento())
                .quantidadeParcelas(cmd.getQuantidadeParcelas())
                .statusPagamento(StatusPagamento.PENDENTE)
                .criadoPor(cmd.getUsuarioId())
                .dataCriacao(DateTimeUtils.getNow().toString())
                .dadosCartao(cmd.getDadosCartao())
                .build();
    }

    /**
     * Processa o pagamento no gateway do Mercado Pago.
     */
    private Pagamento processarPagamentoNoGateway(Pagamento pagamento) {
        log.debug("Enviando pagamento para processamento no gateway. PagamentoId: {}", pagamento.getPagamentoId());
        return this.mercadoPagoGateway.processarPagamento(pagamento);
    }

    /**
     * Salva o pagamento no banco de dados.
     */
    private void salvarPagamento(Pagamento pagamento) {
        log.debug("Salvando pagamento no banco de dados. PagamentoId: {}", pagamento.getPagamentoId());
        this.pagamentoGateway.save(pagamento);
    }

    /**
     * Publica o evento de pagamento conforme o status.
     */
    private void publicarEvento(Pagamento pagamento) {
        if (this.isPagamentoAutorizado(pagamento)) {
            log.info("Publicando evento de pagamento autorizado. OS: {}", pagamento.getOrdemServicoId());
            this.eventPublisher.pagamentoAutorizado(pagamento);
        } else {
            log.info("Publicando evento de pagamento não autorizado. OS: {}, Motivo: {}", pagamento.getOrdemServicoId(), pagamento.getMensagemErro());
            this.eventPublisher.pagamentoNaoAturizado(pagamento);
        }
    }

    /**
     * Verifica se o pagamento foi autorizado.
     */
    private boolean isPagamentoAutorizado(Pagamento pagamento) {
        return pagamento.getStatusPagamento() == StatusPagamento.AUTORIZADO;
    }
}