package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.app.gateways.PagamentoGateway;
import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.infra.pagamento.persistense.PagamentoEntity;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Optional;

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

    @Override
    public Optional<Pagamento> buscarPorOrdemServicoId(String ordemServicoId) {
        Key key = Key.builder()
                .partitionValue(ordemServicoId)
                .build();

        var condition = QueryConditional.keyEqualTo(key);

        var query = QueryEnhancedRequest.builder().queryConditional(condition).build();

        var result = dynamoDbTemplate.query(query, PagamentoEntity.class).items().stream().findFirst();
        return result.map(PagamentoRepositoryMapper::toDomain);
    }
}
