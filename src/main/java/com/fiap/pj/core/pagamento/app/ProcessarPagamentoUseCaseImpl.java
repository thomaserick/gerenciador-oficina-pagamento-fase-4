package com.fiap.pj.core.pagamento.app;


import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.Pagamento;

import java.util.UUID;


public class ProcessarPagamentoUseCaseImpl implements ProcessarPagamentoUseCase {

    private final PagamentoGateway pagamentoGateway;

    public ProcessarPagamentoUseCaseImpl(PagamentoGateway pagamentoGateway) {
        this.pagamentoGateway = pagamentoGateway;
    }


    @Override
    public void handle(ProcessarPagamentoCommand cmd) {
        //Logica para processar o pagamento de api externa
        var pagamento = Pagamento.builder().pagamentoId(UUID.randomUUID().toString()).ordemServicoId(cmd.getOrdemServicoId())
                .clienteId(cmd.getClienteId()).valor(cmd.getValor()).desconto(cmd.getDesconto())
                .valorTotal(cmd.getValorTotal()).metodoPagamento(cmd.getMetodoPagamento()).quantidadeParcelas(cmd.getQuantidadeParcelas()).build();
        pagamentoGateway.save(pagamento);
    }
}
