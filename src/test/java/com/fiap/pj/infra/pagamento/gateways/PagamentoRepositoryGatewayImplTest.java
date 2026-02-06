package com.fiap.pj.infra.pagamento.gateways;

import com.fiap.pj.core.pagamento.domain.Pagamento;
import com.fiap.pj.infra.pagamento.persistense.PagamentoEntity;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PagamentoRepositoryGatewayImplTest {

    private DynamoDbTemplate dynamoDbTemplate;
    private PagamentoRepositoryGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        dynamoDbTemplate = mock(DynamoDbTemplate.class);
        gateway = new PagamentoRepositoryGatewayImpl(dynamoDbTemplate);
    }

    @Test
    void deveSalvarPagamentoNoDynamoDb() {
        Pagamento pagamento = mock(Pagamento.class);
        PagamentoEntity entityMock = mock(PagamentoEntity.class);

        try (MockedStatic<PagamentoRepositoryMapper> mapperMock = mockStatic(PagamentoRepositoryMapper.class)) {
            mapperMock.when(() -> PagamentoRepositoryMapper.toEntity(pagamento))
                    .thenReturn(entityMock);

            gateway.save(pagamento);

            mapperMock.verify(() -> PagamentoRepositoryMapper.toEntity(pagamento), times(1));
            verify(dynamoDbTemplate, times(1)).save(entityMock);
        }
    }
}
