package com.fiap.pj.infra.config;


import com.fiap.pj.core.pagamento.app.BuscarPagamentoUseCaseImpl;
import com.fiap.pj.core.pagamento.app.ProcessarPagamentoUseCaseImpl;
import com.fiap.pj.core.pagamento.app.gateways.MercadoPagoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.app.gateways.PagamentoPublisherGateway;
import com.fiap.pj.core.pagamento.app.usecase.BuscarPagamentoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoConfig {

    @Bean
    ProcessarPagamentoUseCaseImpl processarPagamentoUseCase(PagamentoGateway pagamentoGateway, 
                                                           PagamentoPublisherGateway pagamentoPublisherGateway,
                                                           MercadoPagoGateway mercadoPagoGateway) {
        return new ProcessarPagamentoUseCaseImpl(pagamentoGateway, pagamentoPublisherGateway, mercadoPagoGateway);
    }


    @Bean
    public BuscarPagamentoUseCase buscarPagamentoUseCase(
            PagamentoGateway gateway) {
        return new BuscarPagamentoUseCaseImpl(gateway);
    }

}
