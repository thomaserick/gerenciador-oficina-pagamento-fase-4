package com.fiap.pj.infra.servico.controller.openapi;

import com.fiap.pj.infra.servico.controller.request.ListarPagamentoRequest;
import com.fiap.pj.infra.servico.controller.response.PagamentoResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;

public interface PagamentoControllerOpenApi {

    @Operation(description = "Retorna uma lista de pagamento.", method = "GET")
    PagamentoResponse listarPagamento(@ParameterObject ListarPagamentoRequest filterRequest);


}
