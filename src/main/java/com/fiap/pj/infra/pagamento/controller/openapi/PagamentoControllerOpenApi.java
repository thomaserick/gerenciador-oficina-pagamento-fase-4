package com.fiap.pj.infra.pagamento.controller.openapi;

import com.fiap.pj.core.pagamento.domain.Pagamento;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;

public interface PagamentoControllerOpenApi {

    @Operation(description = "Retorna o pagamento da Ordem de Serviço.", method = "GET")
    Pagamento buscarPagamento(@PathVariable String ordemServicoId);


}
