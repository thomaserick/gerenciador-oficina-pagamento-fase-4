package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.stereotype.Component;

@Component
public class PagamentoRepositoryGatewayImpl implements PagamentoGateway {

    private final DynamoDbTemplate dynamoDbTemplate;

    public PagamentoRepositoryGatewayImpl(DynamoDbTemplate dynamoDbTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
    }

    @Override
    public void save(Pagamento pagamento) {
        dynamoDbTemplate.save(PagamentoRepositoryMapper.toEntity(pagamento));
    }
}
