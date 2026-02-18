package com.fiap.pj.infra.pagamento.consumer;


import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.event.PagamentoProcessadoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoConsumer {

    private final ProcessarPagamentoUseCase processarPagamentoUseCase;

    public PagamentoConsumer(ProcessarPagamentoUseCase processarPagamentoUseCase) {
        this.processarPagamentoUseCase = processarPagamentoUseCase;
    }

    @RabbitListener(queues = "${broker.queue.pagamento.processar}")
    public void receiveMessage(PagamentoProcessadoEvent message) {
        var cmd = new ProcessarPagamentoCommand(
                message.ordemServicoId(),
                message.clienteId(),
                message.valor(),
                message.desconto(),
                message.valorTotal(),
                message.metodoPagamento(),
                message.quantidadeParcelas(),
                message.usuarioId()
        );

        this.processarPagamentoUseCase.handle(cmd);
    }
}
