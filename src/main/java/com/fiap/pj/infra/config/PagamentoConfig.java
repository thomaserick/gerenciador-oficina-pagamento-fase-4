package com.fiap.pj.infra.config;


import com.fiap.pj.core.pagamento.app.ListarPagamentoUseCaseImpl;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoConfig {


    @Bean
    ListarPagamentoUseCaseImpl listarServicoUseCase(PagamentoGateway pagamentoGateway) {
        return new ListarPagamentoUseCaseImpl(pagamentoGateway);
    }

}
