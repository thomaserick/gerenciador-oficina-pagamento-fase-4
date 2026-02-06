package com.fiap.pj.core.pagamento.app;


import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.core.pagamento.domain.StatusPagamento;
import com.fiap.pj.core.util.DateTimeUtils;

import java.util.UUID;


public class ProcessarPagamentoUseCaseImpl implements ProcessarPagamentoUseCase {

    private final PagamentoGateway pagamentoGateway;
    private final PagamentoPublisherGateway eventPublisher;

    public ProcessarPagamentoUseCaseImpl(PagamentoGateway pagamentoGateway, PagamentoPublisherGateway eventPublisher) {
        this.pagamentoGateway = pagamentoGateway;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void handle(ProcessarPagamentoCommand cmd) {
        //Logica para processar o pagamento de api externa
        //Simulando pagamento autorizado
        
        var pagamento = Pagamento.builder()
                .pagamentoId(UUID.randomUUID().toString())
                .ordemServicoId(cmd.getOrdemServicoId())
                .clienteId(cmd.getClienteId())
                .valor(cmd.getValor())
                .desconto(cmd.getDesconto())
                .valorTotal(cmd.getValorTotal())
                .metodoPagamento(cmd.getMetodoPagamento())
                .quantidadeParcelas(cmd.getQuantidadeParcelas())
                .statusPagamento(StatusPagamento.AUTORIZADO)
                .criadoPor(cmd.getResponsavel())
                .dataCriacao(DateTimeUtils.getNow()
                        .toString())
                .build();
        pagamentoGateway.save(pagamento);

        if (pagamento.getStatusPagamento() == StatusPagamento.AUTORIZADO) {
            eventPublisher.pagamentoRealizadoComSucesso(pagamento);
        }

    }
}
