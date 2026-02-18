package com.fiap.pj.infra.pagamento.consumer;


import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.DadosCartao;
import com.fiap.pj.core.pagamento.domain.event.PagamentoProcessadoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PagamentoConsumer {

    private final ProcessarPagamentoUseCase processarPagamentoUseCase;

    public PagamentoConsumer(ProcessarPagamentoUseCase processarPagamentoUseCase) {
        this.processarPagamentoUseCase = processarPagamentoUseCase;
    }

    @RabbitListener(queues = "${broker.queue.pagamento.processar}")
    public void receiveMessage(PagamentoProcessadoEvent message) {
        log.info("Mensagem de pagamento recebida. OS: {}, Valor: {}", message.ordemServicoId(), message.valorTotal());

        DadosCartao dadosCartao = this.mapearDadosCartao(message);

        var cmd = new ProcessarPagamentoCommand(
                message.ordemServicoId(),
                message.clienteId(),
                message.valor(),
                message.desconto(),
                message.valorTotal(),
                message.metodoPagamento(),
                message.quantidadeParcelas(),
                message.usuarioId(),
                dadosCartao
        );

        this.processarPagamentoUseCase.handle(cmd);
    }

    /**
     * Mapeia os dados do cartão do evento para o domínio.
     * Se os dados não estiverem presentes no evento, retorna null (será usado cartão padrão de teste).
     */
    private DadosCartao mapearDadosCartao(PagamentoProcessadoEvent event) {
        if (!event.possuiDadosCartao()) {
            log.debug("Dados do cartão não informados no evento. Será utilizado cartão padrão de teste.");
            return null;
        }

        return DadosCartao.builder()
                .numeroCartao(event.numeroCartao())
                .codigoSeguranca(event.codigoSeguranca())
                .mesExpiracao(event.mesExpiracao())
                .anoExpiracao(event.anoExpiracao())
                .nomeTitular(event.nomeTitular())
                .cpfTitular(event.cpfTitular())
                .emailTitular(event.emailTitular())
                .build();
    }
}