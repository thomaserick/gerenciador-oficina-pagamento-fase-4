package com.fiap.pj.infra.pagamento.controller;

import com.fiap.pj.core.pagamento.app.usecase.BuscarPagamentoUseCase;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.infra.pagamento.controller.openapi.PagamentoControllerOpenApi;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = PagamentoController.PATH)
@AllArgsConstructor
public class PagamentoController implements PagamentoControllerOpenApi {

    public static final String PATH = "v1/pagamentos";

    private final BuscarPagamentoUseCase buscarPagamentoUseCase;


    @Override
    @GetMapping("/ordem-servico/{ordemServicoId}")
    public Pagamento buscarPagamento(@PathVariable String ordemServicoId) {
        return buscarPagamentoUseCase.handle(ordemServicoId);
    }


}
