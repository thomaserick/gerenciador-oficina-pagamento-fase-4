package com.fiap.pj.infra.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {


    private static final String DESCRIPTION = "Documentação de API para Gerenciamento de Oficina Pagamentos.";
    private static final String VERSION = "1.0.0";
    private static final String TITLE = "Gerenciamento de Oficina Pagamentos API";

    @Bean
    public OpenAPI buildOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(TITLE)
                        .description(DESCRIPTION)
                        .version(VERSION));

    }
}