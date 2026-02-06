package com.fiap.pj.infra.config;


import com.fiap.pj.core.pagamento.app.ListarPagamentoUseCaseImpl;
import com.fiap.pj.core.pagamento.app.ProcessarPagamentoUseCaseImpl;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoConfig {

    @Bean
    ProcessarPagamentoUseCaseImpl processarPagamentoUseCase(PagamentoGateway pagamentoGateway, PagamentoPublisherGateway pagamentoPublisherGateway) {
        return new ProcessarPagamentoUseCaseImpl(pagamentoGateway, pagamentoPublisherGateway);
    }


    @Bean
    ListarPagamentoUseCaseImpl listarServicoUseCase(PagamentoGateway pagamentoGateway) {
        return new ListarPagamentoUseCaseImpl(pagamentoGateway);
    }

}
