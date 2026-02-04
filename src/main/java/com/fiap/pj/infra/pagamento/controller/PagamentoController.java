package com.fiap.pj.infra.pagamento.controller;

import com.fiap.pj.core.pagamento.app.usecase.ListarPagamentoUseCase;
import com.fiap.pj.infra.pagamento.controller.openapi.PagamentoControllerOpenApi;
import com.fiap.pj.infra.pagamento.controller.request.ListarPagamentoRequest;
import com.fiap.pj.infra.pagamento.controller.response.PagamentoResponse;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = PagamentoController.PATH)
@AllArgsConstructor
public class PagamentoController implements PagamentoControllerOpenApi {

    public static final String PATH = "v1/pagamentos";

    private final ListarPagamentoUseCase listarPagamentoUseCase;


    @Override
    @GetMapping
    public PagamentoResponse listarPagamento(@ParameterObject ListarPagamentoRequest filterRequest) {
        return listarPagamentoUseCase.handle(filterRequest);
    }


}
