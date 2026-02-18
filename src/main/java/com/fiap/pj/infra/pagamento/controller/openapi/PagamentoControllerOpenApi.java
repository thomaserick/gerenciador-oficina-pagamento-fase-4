package com.fiap.pj.infra.pagamento.controller.openapi;

import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.infra.pagamento.controller.dto.ProcessarPagamentoRequest;
import com.fiap.pj.infra.pagamento.controller.dto.ProcessarPagamentoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Pagamentos", description = "Endpoints para gerenciamento de pagamentos")
public interface PagamentoControllerOpenApi {

    @Operation(
            summary = "Buscar pagamento por Ordem de Serviço",
            description = "Retorna o pagamento associado a uma Ordem de Serviço específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    Pagamento buscarPagamento(@PathVariable String ordemServicoId);

    @Operation(
            summary = "Testar processamento de pagamento",
            description = """
                    Endpoint para testar a integração com o Mercado Pago.
                    
                    Este endpoint simula o recebimento de um evento PagamentoProcessadoEvent 
                    e processa o pagamento diretamente, sem passar pela fila RabbitMQ.
                    
                    **ATENÇÃO:** Este endpoint é apenas para testes em ambiente de desenvolvimento/sandbox.
                    
                    O pagamento será processado utilizando o cartão de teste Mastercard:
                    - Número: 5031 4332 1540 6351
                    - CVV: 123
                    - Validade: 11/30
                    - Titular: APRO (aprovação automática)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagamento processado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProcessarPagamentoResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no processamento")
    })
    ProcessarPagamentoResponse testarPagamento(@RequestBody ProcessarPagamentoRequest request);
}
