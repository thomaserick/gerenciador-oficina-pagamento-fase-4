package com.fiap.pj.core.pagamento.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PagamentoAutorizadoEventTest {

    @Test
    @DisplayName("Deve criar PagamentoRealizadoEvent com ordemServicoId correto")
    void deveCriarPagamentoRealizadoEvent() {
        UUID ordemServicoId = UUID.randomUUID();

        PagamentoEvent event = new PagamentoEvent(ordemServicoId);

        assertThat(event.ordemServicoId()).isEqualTo(ordemServicoId);
    }

}
