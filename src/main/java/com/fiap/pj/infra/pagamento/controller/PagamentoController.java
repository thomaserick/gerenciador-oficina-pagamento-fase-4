package com.fiap.pj.infra.pagamento.controller;

import com.fiap.pj.core.pagamento.app.usecase.BuscarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.ProcessarPagamentoUseCase;
import com.fiap.pj.core.pagamento.app.usecase.command.ProcessarPagamentoCommand;
import com.fiap.pj.core.pagamento.domain.DadosCartao;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.infra.pagamento.controller.dto.ProcessarPagamentoRequest;
import com.fiap.pj.infra.pagamento.controller.dto.ProcessarPagamentoResponse;
import com.fiap.pj.infra.pagamento.controller.openapi.PagamentoControllerOpenApi;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = PagamentoController.PATH)
@AllArgsConstructor
public class PagamentoController implements PagamentoControllerOpenApi {

    public static final String PATH = "v1/pagamentos";

    private final BuscarPagamentoUseCase buscarPagamentoUseCase;
    private final ProcessarPagamentoUseCase processarPagamentoUseCase;

    @Override
    @GetMapping("/ordem-servico/{ordemServicoId}")
    public Pagamento buscarPagamento(@PathVariable String ordemServicoId) {
        return this.buscarPagamentoUseCase.handle(ordemServicoId);
    }

    @Override
    @PostMapping("/integracao-mercado-pago/teste")
    @ResponseStatus(HttpStatus.OK)
    public ProcessarPagamentoResponse testarPagamento(@Valid @RequestBody ProcessarPagamentoRequest request) {
        log.info("Recebida requisição de teste de pagamento. OS: {}, Valor: {}",
                request.ordemServicoId(), request.valorTotal());

        DadosCartao dadosCartao = ProcessarPagamentoCommand.mapearDadosCartao(request.dadosCartao());

        ProcessarPagamentoCommand command = new ProcessarPagamentoCommand(
                request.ordemServicoId(),
                request.clienteId(),
                request.valor(),
                request.getDescontoOuZero(),
                request.valorTotal(),
                request.metodoPagamento(),
                request.getParcelasOuUma(),
                request.getUsuarioOuSistema(),
                dadosCartao
        );

        this.processarPagamentoUseCase.handle(command);

        Pagamento pagamentoProcessado = this.buscarPagamentoUseCase.handle(request.ordemServicoId());

        log.info("Pagamento de teste processado. OS: {}, Status: {}", pagamentoProcessado.getOrdemServicoId(), pagamentoProcessado.getStatusPagamento());
        return ProcessarPagamentoResponse.fromPagamento(pagamentoProcessado);
    }
}
