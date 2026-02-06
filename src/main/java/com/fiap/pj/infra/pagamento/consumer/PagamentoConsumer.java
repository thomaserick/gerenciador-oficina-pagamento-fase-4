package com.fiap.pj.infra.pagamento.consumer;


import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoConsumer {

    private final ProcessarPagamentoUseCase processarPagamentoUseCase;

    public PagamentoConsumer(ProcessarPagamentoUseCase processarPagamentoUseCase) {
        this.processarPagamentoUseCase = processarPagamentoUseCase;
    }

    @RabbitListener(queues = "${broker.queue.pagamento}")
    public void receiveMessage(ProcessarPagamentoMessage message) {
        var cmd = new ProcessarPagamentoCommand(message.ordemServicoId(), message.clienteId(), message.valor(),
                message.desconto(), message.valorTotal(), message.metodoPagamento(), message.quantidadeParcelas(), message.responsavel());
        processarPagamentoUseCase.handle(cmd);
    }
}
